package com.internship.router

import cats.effect.Sync
import cats.implicits._
import com.internship.router.dto.AuthDto
import com.internship.service.UserService
import org.http4s.{EntityEncoder, Header, HttpRoutes}
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl
import com.internship.router.MarshalResponse._
import org.http4s.util.CaseInsensitiveString

object UserRoutes {

  def routes[F[_]: Sync](userService: UserService[F]): HttpRoutes[F] = {

    val dsl = new Http4sDsl[F] {}
    import dsl._

    def logIn(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "portal" / "logIn" =>
      val res = for {
        auth <- req.as[AuthDto]
        answ <- userService.logIn(auth.login, auth.password) //add header
      } yield answ
      marshalResponse(res)
    }

    val LOGIN_HEADER = "loginToken"

    def logOut(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "portal" / "logOut" =>
      val res = for {
        header <- req.headers.get(CaseInsensitiveString(LOGIN_HEADER)).isDefined.pure[F]
        answ   <- userService.logOut(header)
        _      <- req.headers.filterNot(x => x.name == LOGIN_HEADER).pure[F]
      } yield answ
      marshalResponse(res)
    }

    logIn() <+> logOut()
  }

}
