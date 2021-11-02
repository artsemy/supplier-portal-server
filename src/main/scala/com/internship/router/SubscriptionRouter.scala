package com.internship.router

import cats.effect.Sync
import cats.implicits._
import com.internship.router.MarshalResponse.marshalResponse
import com.internship.dto.OrderDto
import com.internship.service.{OrderService, SubscriptionService}
import org.http4s.{HttpRoutes, Request}
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.util.CaseInsensitiveString
import io.circe.generic.auto._
import io.circe._
import io.circe.generic.JsonCodec
import io.circe.parser._
import io.circe.syntax._

object SubscriptionRouter {

  def routes[F[_]: Sync](subscriptionService: SubscriptionService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    def addCategory(): HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ POST -> Root / "portal" / "subs" / "add_cat" / userId / categoryId =>
        val res = for {
          added <- subscriptionService.addSubscriptionCategory(userId, categoryId)
        } yield added
        marshalResponse(res)
    }

    def removeCategory(): HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ POST -> Root / "portal" / "subs" / "remove_cat" / userId / categoryId =>
        val res = for {
          removed <- subscriptionService.removeSubscriptionCategory(userId, categoryId)
        } yield removed
        marshalResponse(res)
    }

    def readAllCategory(): HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ GET -> Root / "portal" / "subs" / "all_cat" / userId =>
        val res = for {
          read <- subscriptionService.readAllSubscriptionCategory(userId)
        } yield read
        marshalResponse(res)
    }

    def addSupplier(): HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ POST -> Root / "portal" / "subs" / "add_sup" / userId / categoryId =>
        val res = for {
          added <- subscriptionService.addSubscriptionSupplier(userId, categoryId)
        } yield added
        marshalResponse(res)
    }

    def removeSupplier(): HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ POST -> Root / "portal" / "subs" / "remove_sup" / userId / categoryId =>
        val res = for {
          removed <- subscriptionService.removeSubscriptionSupplier(userId, categoryId)
        } yield removed
        marshalResponse(res)
    }

    def readAllSupplier(): HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ GET -> Root / "portal" / "subs" / "all_sup" / userId =>
        val res = for {
          read <- subscriptionService.readAllSubscriptionSupplier(userId)
        } yield read
        marshalResponse(res)
    }

    addCategory() <+> removeCategory() <+> readAllCategory() <+> addSupplier() <+> removeSupplier() <+> readAllSupplier()
  }

}
