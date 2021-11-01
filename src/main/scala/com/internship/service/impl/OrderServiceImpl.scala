package com.internship.service.impl

import cats.Monad
import cats.implicits._
import com.internship.dao.OrderDAO
import com.internship.domain.{Order, OrderProduct, OrderStatus}
import com.internship.dto.OrderDto
import com.internship.error.OrderError
import com.internship.service.OrderService
import com.internship.service.validation.OrderValidator
import com.internship.util.TraverseEitherTupleUtil.{traverseThreeTypes, traverseTwoTypes}
import io.chrisdavenport.log4cats.Logger
//import org.typelevel.log4cats.Logger

class OrderServiceImpl[F[_]: Monad: Logger](orderDAO: OrderDAO[F]) extends OrderService[F] {

  override def create(orderDto: OrderDto): F[Either[OrderError, Int]] = for {
    _   <- Logger[F].info("order service create: try")
    res <- OrderValidator.validate(orderDto).traverse(x => orderDAO.create(x))
    _   <- Logger[F].info("order service create: finish")
  } yield res

  override def read(orderId: String): F[Either[OrderError, Option[OrderDto]]] = for {
    _ <- Logger[F].info("order service read: try")
    res <- OrderValidator
      .validateOrderId(orderId)
      .traverse(x => orderDAO.read(x).map(y => y.map(z => convertOrderToDto(z))))
    _ <- Logger[F].info("order service read: finish")
  } yield res

  override def delete(orderId: String): F[Either[OrderError, Int]] = for {
    _ <- Logger[F].info("order service delete: try")
    res <- OrderValidator
      .validateOrderId(orderId)
      .traverse(x => orderDAO.delete(x))
    _ <- Logger[F].info("order service delete: finish")
  } yield res

  override def update(orderId: String, orderDto: OrderDto): F[Either[OrderError, Int]] = for {
    _     <- Logger[F].info("order service update: try")
    id    <- OrderValidator.validateOrderId(orderId).pure[F]
    order <- OrderValidator.validate(orderDto).pure[F]
    res   <- traverseTwoTypes(id, order).traverse { case (id, order) => orderDAO.update(id, order) }
    _     <- Logger[F].info("order service update: finish")
  } yield res

  override def addProduct(orderId: String, productId: String, amount: String): F[Either[OrderError, Int]] = for {
    _   <- Logger[F].info("order service add product: try")
    id1 <- OrderValidator.validateOrderId(orderId).pure[F]
    id2 <- OrderValidator.validateOrderProductId(productId).pure[F]
    am  <- OrderValidator.validateOrderProductAmount(amount).pure[F]
    res <- traverseThreeTypes(id1, id2, am).traverse { case (id1, id2, am) => orderDAO.addProduct(id1, id2, am) }
    _   <- Logger[F].info("order service add product: finish")
  } yield res

  override def removeProduct(orderId: String, productId: String): F[Either[OrderError, Int]] = for {
    _   <- Logger[F].info("order service remove product: try")
    id1 <- OrderValidator.validateOrderId(orderId).pure[F]
    id2 <- OrderValidator.validateOrderProductId(productId).pure[F]
    res <- traverseTwoTypes(id1, id2).traverse { case (id1, id2) => orderDAO.removeProduct(id1, id2) }
    _   <- Logger[F].info("order service remove product: finish")
  } yield res

  override def updateProductAmount(orderProductId: String, amount: String): F[Either[OrderError, Int]] = for {
    _   <- Logger[F].info("order service update product amount: try")
    id  <- OrderValidator.validateOrderProductId(orderProductId).pure[F]
    am  <- OrderValidator.validateOrderProductAmount(amount).pure[F]
    res <- traverseTwoTypes(id, am).traverse { case (id, am) => orderDAO.updateProductAmount(id, am) }
    _   <- Logger[F].info("order service update product amount: finish")
  } yield res

  override def readAllProductInOrder(orderId: String): F[Either[OrderError, Map[Long, OrderProduct]]] = for {
    _   <- Logger[F].info("order service read all product in order: try")
    id  <- OrderValidator.validateOrderId(orderId).pure[F]
    res <- id.traverse { x => orderDAO.readAllProductInOrder(x) }
    _   <- Logger[F].info("order service read all product in order: finish")
  } yield res

  override def changeStatus(orderId: String, orderDto: OrderDto, orderStatus: String): F[Either[OrderError, Int]] =
    for {
      _           <- Logger[F].info("order service change status: try")
      orderId     <- OrderValidator.validateOrderId(orderId).pure[F]
      order       <- OrderValidator.validate(orderDto).pure[F]
      oldStatus   <- order.getOrElse(Order(0, 0, OrderStatus.inProcessing, "")).orderStatus.pure[F]
      newStatus   <- OrderValidator.validateOrderStatus(orderStatus).getOrElse(OrderStatus.inProcessing).pure[F]
      validStatus <- OrderValidator.validateOrderNextStatus(oldStatus, newStatus).pure[F]
      res <- traverseThreeTypes(orderId, order, validStatus).traverse { case (id, order, status) =>
        orderDAO.update(id, order.copy(orderStatus = status))
      }
      _ <- Logger[F].info("order service change status: finish")
    } yield res

  def convertOrderToDto(order: Order): OrderDto = {
    OrderDto(
      order.ownerId.toString,
      order.courierId.toString,
      order.orderStatus.toString,
      order.address
    )
  }

}
