package com.internship.dao

import cats.effect.Sync
import com.internship.dao.impl.DoobieOrderDAO
import com.internship.domain.{Order, OrderProduct}
import doobie.util.transactor.Transactor

trait OrderDAO[F[_]] {
  def create(order:   Order): F[Int]
  def read(orderId:   Long):  F[Option[Order]]
  def delete(orderId: Long): F[Int]
  def update(orderId: Long, order: Order): F[Int]

  def addProduct(orderId:                 Long, productId: Long, amount: Int): F[Int]
  def removeProduct(orderId:              Long, productId: Long): F[Int]
  def updateProductAmount(orderProductId: Long, amount: Int): F[Int]
  def readAllProductInOrder(orderId:      Long): F[Map[Long, OrderProduct]]
}

object OrderDAO {
  def of[F[_]: Sync](tx: Transactor[F]): OrderDAO[F] = new DoobieOrderDAO[F](tx)
}
