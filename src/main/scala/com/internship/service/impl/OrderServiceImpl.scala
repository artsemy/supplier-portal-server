package com.internship.service.impl

import cats.Monad
import cats.implicits._
import com.internship.dao.OrderDAO
import com.internship.domain.{Order, OrderProduct, OrderStatus}
import com.internship.dto.OrderDto
import com.internship.error.OrderError
import com.internship.service.OrderService
import com.internship.service.validation.OrderValidator

class OrderServiceImpl[F[_]: Monad](orderDAO: OrderDAO[F]) extends OrderService[F] {

  override def create(orderDto: OrderDto): F[Either[OrderError, Int]] = {
    OrderValidator.validate(orderDto).traverse(x => orderDAO.create(x))
  }

  override def read(orderId: String): F[Either[OrderError, Option[OrderDto]]] = {
    OrderValidator
      .validateOrderId(orderId)
      .traverse(x => orderDAO.read(x).map(y => y.map(z => convertOrderToDto(z))))
  }

  override def delete(orderId: String): F[Either[OrderError, Int]] = {
    OrderValidator
      .validateOrderId(orderId)
      .traverse(x => orderDAO.delete(x))
  }

  override def update(orderId: String, orderDto: OrderDto): F[Either[OrderError, Int]] = {
    val init = for {
      id    <- OrderValidator.validateOrderId(orderId)
      order <- OrderValidator.validate(orderDto)
    } yield (id, order)
    init.traverse { case (id, order) =>
      orderDAO.update(id, order)
    }
  }

  override def addProduct(orderId: String, productId: String, amount: String): F[Either[OrderError, Int]] = {
    val init = for {
      id1 <- OrderValidator.validateOrderId(orderId)
      id2 <- OrderValidator.validateOrderProductId(productId)
      am  <- OrderValidator.validateOrderProductAmount(amount)
    } yield (id1, id2, am)
    init.traverse { case (id1, id2, am) =>
      orderDAO.addProduct(id1, id2, am)
    }
  }

  override def removeProduct(orderId: String, productId: String): F[Either[OrderError, Int]] = {
    val init = for {
      id1 <- OrderValidator.validateOrderId(orderId)
      id2 <- OrderValidator.validateOrderProductId(productId)
    } yield (id1, id2)
    init.traverse { case (id1, id2) =>
      orderDAO.removeProduct(id1, id2)
    }
  }

  override def updateProductAmount(orderProductId: String, amount: String): F[Either[OrderError, Int]] = {
    val init = for {
      id <- OrderValidator.validateOrderProductId(orderProductId)
      am <- OrderValidator.validateOrderProductAmount(amount)
    } yield (id, am)
    init.traverse { case (id, am) =>
      orderDAO.updateProductAmount(id, am)
    }
  }

  override def readAllProductInOrder(orderId: String): F[Either[OrderError, Map[Long, OrderProduct]]] = {
    OrderValidator
      .validateOrderId(orderId)
      .traverse { x =>
        orderDAO.readAllProductInOrder(x)
      }
  }

  override def changeStatus(orderId: String, orderDto: OrderDto, orderStatus: String): F[Either[OrderError, Int]] = {
    val res = for {
      orderId     <- OrderValidator.validateOrderId(orderId)
      order       <- OrderValidator.validate(orderDto)
      oldStatus    = order.orderStatus
      newStatus   <- OrderValidator.validateOrderStatus(orderStatus)
      validStatus <- OrderValidator.validateOrderNextStatus(oldStatus, newStatus)
    } yield (orderId, order, validStatus)
    res.traverse { case (id, order, status) =>
      orderDAO.update(id, order.copy(orderStatus = status))
    }
  }

  def convertOrderToDto(order: Order): OrderDto = {
    OrderDto(
      order.ownerId.toString,
      order.courierId.toString,
      order.orderStatus.toString,
      order.address
    )
  }

}
