package com.internship.dao

import cats.effect.Sync
import com.internship.dao.impl.DoobieProductDAO
import com.internship.domain.Product
import com.internship.dto.SmartSearchDto
import doobie.util.transactor.Transactor

trait ProductDAO[F[_]] {
  def create(product:   Product): F[Int]
  def read(productId:   Long): F[Option[Product]]
  def update(productId: Long, product: Product): F[Int]
  def delete(productId: Long):    F[Int]
  def readAll(): F[Map[Long, Product]]
  def search(searchLine:          String):         F[Map[Long, Product]]
  def smartSearch(smartSearchDto: SmartSearchDto): F[Map[Long, Product]]
}

object ProductDAO {
  def of[F[_]: Sync](tx: Transactor[F]): ProductDAO[F] = new DoobieProductDAO[F](tx)
}
