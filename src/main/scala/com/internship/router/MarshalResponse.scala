package com.internship.router

import cats.effect.Sync
import cats.implicits._
import org.http4s.{EntityEncoder, Header, Response}
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl
import com.internship.error.SupplierPortalError

object MarshalResponse {

  def marshalResponse[F[_]: Sync, T, K <: SupplierPortalError](
    result: F[Either[K, T]]
  )(
    implicit E: EntityEncoder[F, T]
  ): F[Response[F]] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    def UserErrorToHttpResponse(error: K): F[Response[F]] =
      error match {
        case e => BadRequest(e.message)
      }

    result
      .flatMap {
        case Left(error) => UserErrorToHttpResponse(error)
        case Right(dto)  => Ok(dto)
      }
      .handleErrorWith { ex =>
        InternalServerError(ex.getMessage)
      }
  }

}
