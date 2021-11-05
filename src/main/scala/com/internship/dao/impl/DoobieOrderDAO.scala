package com.internship.dao.impl

import cats.Functor
import cats.effect.Bracket
import com.internship.dao.OrderDAO
import com.internship.domain.{Order, OrderProduct}
import doobie.util.transactor.Transactor
import com.internship.dao.impl.meta.implicits._
import doobie.implicits._

case class DoobieOrderDAO[F[_]: Functor: Bracket[*[_], Throwable]](tx: Transactor[F]) extends OrderDAO[F] {

  val createOrder = fr"insert into orders "
  val readOrder   = fr"select * from orders "
  val deleteOrder = fr"delete from orders "
  val updateOrder = fr"update orders set "

  override def create(order: Order): F[Int] = {
    val fr = createOrder ++
      fr"(owner_id, courier_id, orders_status, address)" ++
      fr"values " ++
      fr"(${order.ownerId}, ${order.courierId}, ${order.orderStatus}, ${order.address});"
    fr.update.withUniqueGeneratedKeys[Int]("id").transact(tx)
  }

  override def read(orderId: Long): F[Option[Order]] = {
    val fr = readOrder ++ fr"where id = $orderId"
    fr.query[(Long, Order)].map { case (l, order) => order }.option.transact(tx)
  }

  override def delete(orderId: Long): F[Int] = {
    val fr = deleteOrder ++
      fr"where id = $orderId"
    fr.update.run.transact(tx)
  }

  override def update(orderId: Long, order: Order): F[Int] = {
    val fr = updateOrder ++
      fr"owner_id = ${order.ownerId}, " ++
      fr"courier_id = ${order.courierId}, " ++
      fr"orders_status = ${order.orderStatus}," ++
      fr"address = ${order.address} " ++
      fr"where id = $orderId"
    fr.update.run.transact(tx)
  }

  val insertOrderProduct     = fr"insert into orders_product "
  val removeProductFromOrder = fr"delete from orders_product "
  val updateProductAmount    = fr"update orders_product set "

  override def addProduct(orderId: Long, productId: Long, amount: Int): F[Int] = {
    val fr = insertOrderProduct ++
      fr"(orders_id, product_id, amount) " ++
      fr"values " ++
      fr"($orderId, $productId, $amount);"
    fr.update.withUniqueGeneratedKeys[Int]("id").transact(tx)
  }

  override def removeProduct(orderId: Long, productId: Long): F[Int] = {
    val fr = removeProductFromOrder ++
      fr"where orders_id = $orderId and product_id = $productId"
    fr.update.run.transact(tx)
  }

  override def updateProductAmount(orderProductId: Long, amount: Int): F[Int] = {
    val fr = updateProductAmount ++
      fr"amount = $amount" ++
      fr"where id = $orderProductId"
    fr.update.run.transact(tx)
  }

  override def readAllProductInOrder(orderId: Long): F[Map[Long, OrderProduct]] = {
    val fr = fr"select o.id, p.name, o.amount from " ++
      fr"product p join orders_product o on o.product_id = p.id where " ++
      fr"orders_id = $orderId"
    fr.query[(Long, OrderProduct)].toMap.transact(tx)
  }

}
