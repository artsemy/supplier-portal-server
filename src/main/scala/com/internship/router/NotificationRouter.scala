package com.internship.router

import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl

object NotificationRouter {

  def routes[F[_]: Sync](): HttpRoutes[F] = {

    val dsl = new Http4sDsl[F] {}
    import dsl._

    def go(): HttpRoutes[F] = HttpRoutes.of[F] { case GET -> Root => Ok("never works") }

    go()

  }

}
