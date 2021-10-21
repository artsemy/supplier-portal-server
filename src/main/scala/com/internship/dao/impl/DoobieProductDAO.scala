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

  override def create(product: Product): F[Int] = {
    val fr = fr"insert into product" ++
      fr"(name, publication_date, update_date, description, price, supplier_id, product_status) " ++
      fr"values " ++
      fr"(${product.name}, ${product.publicationDate}, ${product.updateDAte}, ${product.description}, " ++
      fr"${product.price}, ${product.supplierId}, ${product.productStatus});"
    fr.update.withUniqueGeneratedKeys[Int]("id").transact(tx)
  }

  override def read(productId: Long): F[Option[Product]] = {
    val fr = fr"select * from product" ++
      fr"where id = $productId"
    fr.query[(Long, Product)]
      .map { case (_, product) =>
        product
      }
      .option
      .transact(tx)
  }

  override def update(productId: Long, product: Product): F[Int] = {
    val fr = fr"update product set name = ${product.name}, " ++
      fr"publication_date = ${product.publicationDate}, " ++
      fr"update_date = ${product.updateDAte}, " ++
      fr"description = ${product.description}, " ++
      fr"price = ${product.price}, " ++
      fr"supplier_id = ${product.supplierId}, " ++
      fr"product_status = ${product.productStatus} " ++
      fr"where id = $productId"
    fr.update.run.transact(tx)
  }

  override def delete(productId: Long): F[Int] = {
    val fr = fr"delete from product where id = $productId"
    fr.update.run.transact(tx)
  }

}
