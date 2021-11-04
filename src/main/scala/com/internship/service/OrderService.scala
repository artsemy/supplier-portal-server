package com.internship.service

import cats.effect.Sync
import com.internship.dao.OrderDAO
import com.internship.domain.OrderProduct
import com.internship.dto.OrderDto
import com.internship.error.OrderError
import com.internship.service.impl.OrderServiceImpl

trait OrderService[F[_]] {
  def create(orderDto: OrderDto): F[Either[OrderError, Int]]
  def read(orderId:    String):   F[Either[OrderError, Option[OrderDto]]]
  def delete(orderId:  String): F[Either[OrderError, Int]]
  def update(orderId:  String, orderDto: OrderDto): F[Either[OrderError, Int]]

  def addProduct(orderId:                 String, productId: String, amount: String): F[Either[OrderError, Int]]
  def removeProduct(orderId:              String, productId: String): F[Either[OrderError, Int]]
  def updateProductAmount(orderProductId: String, amount: String): F[Either[OrderError, Int]]
  def readAllProductInOrder(orderId:      String): F[Either[OrderError, Map[Long, OrderProduct]]]

  def changeStatus(orderId: String, orderDto: OrderDto, orderStatus: String): F[Either[OrderError, Int]]
}

object OrderService {
  def of[F[_]: Sync](orderDAO: OrderDAO[F]): OrderService[F] = new OrderServiceImpl[F](orderDAO)
}
