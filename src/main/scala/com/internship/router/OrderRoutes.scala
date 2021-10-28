package com.internship.router

import cats.effect.Sync
import cats.implicits._
import com.internship.router.MarshalResponse.marshalResponse
import com.internship.dto.OrderDto
import com.internship.service.OrderService
import org.http4s.{HttpRoutes, Request}
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.util.CaseInsensitiveString
import io.circe.generic.auto._
import io.circe._
import io.circe.generic.JsonCodec
import io.circe.parser._
import io.circe.syntax._

object OrderRoutes {

  def routes[F[_]: Sync](orderService: OrderService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    def create(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ POST -> Root / "portal" / "order" / "create" =>
      val res = for {
        orderDto <- req.as[OrderDto]
        created  <- orderService.create(orderDto)
      } yield created
      marshalResponse(res)
    }

    def read(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "portal" / "order" / "read" / id =>
      val res = orderService.read(id)
      marshalResponse(res)
    }

    def delete(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ POST -> Root / "portal" / "order" / "delete" / id =>
      val res = orderService.delete(id)
      marshalResponse(res)
    }

    def update(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ POST -> Root / "portal" / "order" / "update" / id =>
      val res = for {
        orderDto <- req.as[OrderDto]
        updated  <- orderService.update(id, orderDto)
      } yield updated
      marshalResponse(res)
    }

    def addProduct(): HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ POST -> Root / "portal" / "order" / "add_product" / orderId / productId / amount =>
        val res = orderService.addProduct(orderId, productId, amount)
        marshalResponse(res)
    }

    def removeProduct(): HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ POST -> Root / "portal" / "order" / "remove_product" / orderId / productId =>
        val res = orderService.removeProduct(orderId, productId)
        marshalResponse(res)
    }

    def updateProductAmount(): HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ POST -> Root / "portal" / "order" / "update_amount" / orderProductId / amount =>
        val res = orderService.updateProductAmount(orderProductId, amount)
        marshalResponse(res)
    }

    def readAllProductInOrder(): HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ GET -> Root / "portal" / "order" / "all_product" / orderId =>
        val res = orderService.readAllProductInOrder(orderId)
        marshalResponse(res)
    }

    def changeStatus(): HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ POST -> Root / "portal" / "order" / "set_status" / orderId / newStatus =>
        val res = for {
          orderDto <- req.as[OrderDto]
          set      <- orderService.changeStatus(orderId, orderDto, newStatus)
        } yield set
        marshalResponse(res)
    }

    create() <+> read() <+> delete() <+> update() <+> addProduct() <+> removeProduct() <+>
      updateProductAmount() <+> readAllProductInOrder() <+> changeStatus()
  }

}
