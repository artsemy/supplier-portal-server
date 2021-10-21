package com.internship.router

import cats.effect.Sync
import cats.implicits._
import com.internship.error.{ProductError, SupplierPortalError}
import com.internship.router.dto.ProductDto
import com.internship.service.ProductService
import org.http4s.{EntityEncoder, HttpRoutes, Response}
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl

object ProductRoutes {

  def routes[F[_]: Sync](productService: ProductService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    def create(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "portal" / "product" / "create" =>
      val res = for {
        productDto <- req.as[ProductDto]
        answ       <- productService.create(productDto)
      } yield answ
      marshalResponse(res)
    }

    def read(): HttpRoutes[F] = HttpRoutes.of[F] { case GET -> Root / "portal" / "product" / "read" / id =>
      val res = for {
        product <- productService.read(id)
      } yield product
      marshalResponse(res)
    }

    def UserErrorToHttpResponse(error: ProductError): F[Response[F]] = //switch to SupplierPortalError
      error match {
        case e => BadRequest(e.message)
      }

    def marshalResponse[T](
      result: F[Either[ProductError, T]] //switch to SupplierPortalError
    )(
      implicit E: EntityEncoder[F, T]
    ): F[Response[F]] =
      result
        .flatMap {
          case Left(error) => UserErrorToHttpResponse(error)
          case Right(dto)  => Ok(dto)
        }
        .handleErrorWith { ex =>
          InternalServerError(ex.getMessage)
        }

    create() <+> read()
  }

}
