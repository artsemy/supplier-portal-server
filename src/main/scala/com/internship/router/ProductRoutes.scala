package com.internship.router

import cats.effect.Sync
import cats.implicits._
import com.internship.router.MarshalResponse.marshalResponse
import com.internship.router.dto.ProductDto
import com.internship.service.ProductService
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl

object ProductRoutes {

  def routes[F[_]: Sync](productService: ProductService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    def create(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "portal" / "product" / "create" =>
      val res = for {
        productDto <- req.as[ProductDto]
        //validate input
        //check role
        answ <- productService.create(productDto)
      } yield answ
      marshalResponse(res)
    }

    def read(): HttpRoutes[F] = HttpRoutes.of[F] { case GET -> Root / "portal" / "product" / "read" / id =>
      val res = for {
        product <- productService.read(id)
      } yield product
      marshalResponse(res)
    }

    create() <+> read() // <+> update() <+> delete()
  }

}
