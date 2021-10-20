package com.internship.router

import cats.effect.Sync
import cats.implicits._
import com.internship.dao.error.UserDAOError
import com.internship.router.dto.AuthDto
import com.internship.service.UserService
import org.http4s.{EntityEncoder, HttpRoutes, Response}
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl

object UserRoutes {

  def routes[F[_]: Sync](userService: UserService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    def logIn(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "portal" / "logIn" =>
      val res = for {
        auth <- req.as[AuthDto]
        answ <- userService.logIn(auth.login, auth.password)
      } yield answ
      marshalResponse(res)
    }

    def test(): HttpRoutes[F] = HttpRoutes.of[F] { case GET -> Root / "portal" / "test" =>
      val res: Either[UserDAOError, String] = Right("true")
      marshalResponse(res.pure[F])
    }

    def gameErrorToHttpResponse(error: UserDAOError): F[Response[F]] =
      error match {
        case e => BadRequest(e.message)
      }

    def marshalResponse[T](
      result: F[Either[UserDAOError, T]]
    )(
      implicit E: EntityEncoder[F, T]
    ): F[Response[F]] =
      result
        .flatMap {
          case Left(error) => gameErrorToHttpResponse(error)
          case Right(dto)  => Ok(dto)
        }
        .handleErrorWith { ex =>
          InternalServerError(ex.getMessage)
        }

    logIn() <+> test()
  }

}
