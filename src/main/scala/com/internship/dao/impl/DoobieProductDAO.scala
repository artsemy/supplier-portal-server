package com.internship.dao.impl

import cats.Functor
import cats.effect.Bracket
import com.internship.dao.ProductDAO
import com.internship.domain.Product
import doobie.util.transactor.Transactor

import com.internship.dao.impl.meta.implicits._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.implicits.javatime._
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.postgres.pgisimplicits._

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

}
