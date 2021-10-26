package com.internship.dao

import cats.effect.Sync
import com.internship.dao.impl.DoobieProductDAO
import com.internship.domain.Product
import doobie.util.transactor.Transactor

trait ProductDAO[F[_]] {

  def create(product:   Product): F[Int]
  def read(productId:   Long): F[Option[Product]]
  def update(productId: Long, product: Product): F[Int]
  def delete(productId: Long):    F[Int]
  def readAll(): F[Map[Long, Product]]

}

object ProductDAO {
  def of[F[_]: Sync](tx: Transactor[F]): ProductDAO[F] = new DoobieProductDAO[F](tx)
}
