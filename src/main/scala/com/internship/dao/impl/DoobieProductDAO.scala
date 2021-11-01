package com.internship.dao.impl

import cats.Functor
import cats.effect.Bracket
import com.internship.dao.ProductDAO
import com.internship.domain.Product
import doobie.util.transactor.Transactor
import com.internship.dao.impl.meta.implicits._
import com.internship.dto.SmartSearchDto
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.implicits.javatime._
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.postgres.pgisimplicits._
import doobie.util.fragment.Fragment

class DoobieProductDAO[F[_]: Functor: Bracket[*[_], Throwable]](tx: Transactor[F]) extends ProductDAO[F] {

  val selectFullProduct = fr"select * from product "
  val insertProduct     = fr"insert into product "
  val updateProduct     = fr"update product set "
  val deleteProduct     = fr"delete from product "

  override def create(product: Product): F[Int] = {
    val fr = insertProduct ++
      fr"(name, publication_date, update_date, description, price, supplier_id, product_status) " ++
      fr"values " ++
      fr"(${product.name}, ${product.publicationDate}, ${product.updateDate}, ${product.description}, " ++
      fr"${product.price}, ${product.supplierId}, ${product.productStatus});"
    fr.update.withUniqueGeneratedKeys[Int]("id").transact(tx)
  }

  override def read(productId: Long): F[Option[Product]] = {
    val fr = selectFullProduct ++
      fr"where id = $productId"
    fr.query[(Long, Product)]
      .map { case (_, product) =>
        product
      }
      .option
      .transact(tx)
  }

  override def update(productId: Long, product: Product): F[Int] = {
    val fr = updateProduct ++
      fr"name = ${product.name}, " ++
      fr"publication_date = ${product.publicationDate}, " ++
      fr"update_date = ${product.updateDate}, " ++
      fr"description = ${product.description}, " ++
      fr"price = ${product.price}, " ++
      fr"supplier_id = ${product.supplierId}, " ++
      fr"product_status = ${product.productStatus} " ++
      fr"where id = $productId"
    fr.update.run.transact(tx)
  }

  override def delete(productId: Long): F[Int] = {
    val fr = deleteProduct ++
      fr"where id = $productId"
    fr.update.run.transact(tx)
  }

  override def readAll(): F[Map[Long, Product]] = {
    val fr = selectFullProduct
    fr.query[(Long, Product)]
      .toMap
      .transact(tx)
  }

  override def smartSearch(smartSearchDto: SmartSearchDto): F[Map[Long, Product]] = {
    val line = buildLine(smartSearchDto)
    val fr = fr"select p.id, name ,publication_date, update_date, description, price, supplier_id, product_status " ++
      fr"from product p JOIN product_category c ON p.id = c.product_id WHERE " ++ Fragment.const(line)
    fr.query[(Long, Product)]
      .toMap
      .transact(tx)
  }

  //careful column name because of JOIN
  private def buildLine(dto: SmartSearchDto): String = {
    List(
      exactSearchLine("name", dto.name),
      exactSearchLine("publication_date", dto.pubDate),
      exactSearchLine("update_date", dto.upDate),
      exactSearchLine("description", dto.description),
      exactSearchLine("price", dto.price),
      exactSearchLine("supplier_id", dto.supplierId),
      exactSearchLine("product_status", dto.productStatus),
      periodSearchLine("publication_date", dto.pubDatePeriod),
      periodSearchLine("update_date", dto.upDatePeriod),
      inArraySearchLine("category_id", dto.listCategoryId)
    ).filter(x => x != "").reduce(_ + " AND " + _)
  }

  def exactSearchLine(columnName: String, value: Option[String]): String = {
    value.map(x => s"$columnName LIKE '%$x%'").getOrElse("")
  }

  def periodSearchLine(columnName: String, value: Option[(String, String)]): String = {
    value.map { case (st, end) => s"$columnName BETWEEN '$st' AND '$end'" }.getOrElse("")
  }

  def inArraySearchLine(columnName: String, arr: Option[List[Int]]): String = {
    val l1 = arr.map(x => x.map(_.toString).reduce(_ + ", " + _))
    l1.map(x => s"$columnName IN ($x)").getOrElse("")
  }

}
