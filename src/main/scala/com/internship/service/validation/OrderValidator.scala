package com.internship.service.validation

import com.internship.domain.{Order, OrderStatus}
import com.internship.dto.OrderDto
import com.internship.error.OrderError

object OrderValidator {

  trait OrderValidationError extends OrderError

  object OrderValidationError {

    case object OrderOwnerIdFormat extends OrderValidationError {
      override def message: String = "order owner id format"
    }

    case object OrderCourierIdFormat extends OrderValidationError {
      override def message: String = "order courier id format"
    }

    case object OrderStatusFormat extends OrderValidationError {
      override def message: String = "order status format"
    }

    case object OrderAddressFormat extends OrderValidationError {
      override def message: String = "order address format"
    }

    case object OrderIdFormat extends OrderValidationError {
      override def message: String = "order id format"
    }

    case object OrderProductIdFormat extends OrderValidationError {
      override def message: String = "order_product_db product id format"
    }

    case object OrderProductAmountFormat extends OrderValidationError {
      override def message: String = "order_product_db amount format"
    }

    case object OrderNextStatusFormat extends OrderValidationError {
      override def message: String = "cant switch to new status"
    }

  }

  import OrderValidationError._

  def validate(orderDto: OrderDto): Either[OrderValidationError, Order] = for {
    ownerId     <- validateOwnerId(orderDto.ownerId)
    courierId   <- validateCourierId(orderDto.courierId)
    orderStatus <- validateOrderStatus(orderDto.orderStatus)
    address     <- validateAddress(orderDto.address)
  } yield Order(ownerId, courierId, orderStatus, address)

  def validateOwnerId(id: String): Either[OrderValidationError, Long] = {
    id.toLongOption.toRight(OrderOwnerIdFormat)
  }

  def validateCourierId(id: String): Either[OrderValidationError, Long] = {
    id.toLongOption.toRight(OrderCourierIdFormat)
  }

  def validateOrderStatus(status: String): Either[OrderValidationError, OrderStatus] = {
    OrderStatus.withNameInsensitiveEither(status).left.map(_ => OrderStatusFormat)
  }

  def validateAddress(address: String): Either[OrderValidationError, String] = {
    val res: Either[OrderValidationError, String] =
      if (address.matches("[\\w\\s]+"))
        Right(address)
      else
        Left(OrderAddressFormat)
    res
  }

  def validateOrderId(id: String): Either[OrderValidationError, Long] = {
    id.toLongOption.toRight(OrderIdFormat)
  }

  def validateOrderProductId(id: String): Either[OrderValidationError, Long] = {
    id.toLongOption.toRight(OrderProductIdFormat)
  }

  def validateOrderProductAmount(amount: String): Either[OrderValidationError, Int] = {
    amount.toIntOption.toRight(OrderProductAmountFormat)
  }

  def validateOrderNextStatus(
    oldStatus: OrderStatus,
    newStatus: OrderStatus
  ): Either[OrderValidationError, OrderStatus] = {
    val map = OrderStatus.valuesToIndex
    val res: Either[OrderValidationError, OrderStatus] =
      if (map.getOrElse(newStatus, 0) - map.getOrElse(oldStatus, 0) == 1)
        Right(newStatus)
      else
        Left(OrderNextStatusFormat)
    res
  }

}
