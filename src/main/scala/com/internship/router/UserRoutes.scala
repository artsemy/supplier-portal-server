package com.internship.router

import cats.effect.Sync
import cats.implicits._
import com.internship.dto.AuthDto
import com.internship.service.UserService
import org.http4s.{EntityEncoder, Header, HeaderKey, HttpRoutes}
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl
import com.internship.router.MarshalResponse._
import org.http4s.util.CaseInsensitiveString

object UserRoutes {

  def routes[F[_]: Sync](userService: UserService[F]): HttpRoutes[F] = {

    val dsl = new Http4sDsl[F] {}
    import dsl._

    val LOGIN_HEADER_TOKEN = "loginToken"

    def logIn(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "portal" / "logIn" =>
      for {
        auth  <- req.as[AuthDto]
        answ   = userService.logIn(auth)
        token <- userService.generateToken(auth)
        _      = println(token)
        res   <- marshalResponse(answ).map(x => x.putHeaders(Header(LOGIN_HEADER_TOKEN, token)))
      } yield res
    }

    def logOut(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "portal" / "logOut" =>
      for {
        headerExists <- req.headers.get(CaseInsensitiveString(LOGIN_HEADER_TOKEN)).isDefined.pure[F]
        answ          = userService.logOut(headerExists)
        res          <- marshalResponse(answ).map(x => x.filterHeaders(x => x.name != CaseInsensitiveString(LOGIN_HEADER_TOKEN)))
      } yield res
    }

    logIn() <+> logOut()
  }

}
