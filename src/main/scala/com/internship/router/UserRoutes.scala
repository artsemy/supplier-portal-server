package com.internship.router

import cats.effect.Sync
import cats.implicits._
import com.internship.constant.ConstantStrings.{LOGIN_HEADER_TOKEN, LOG_IN_MESSAGE}
import com.internship.dto.AuthDto
import com.internship.service.UserService
import org.http4s.{EntityEncoder, Header, HeaderKey, HttpRoutes}
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl
import com.internship.router.MarshalResponse._
import com.internship.util.TokenUtil
import org.http4s.util.CaseInsensitiveString

object UserRoutes {

  def routes[F[_]: Sync](userService: UserService[F]): HttpRoutes[F] = {

    val dsl = new Http4sDsl[F] {}
    import dsl._

    def logIn(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "portal" / "user" / "logIn" =>
      for {
        auth <- req.as[AuthDto]
        user <- userService.logIn(auth)
        token = TokenUtil.generateToken(user)
//        _      = println(token.getOrElse("token not created"))
        res <- token match {
          case Left(_) => marshalResponse(token.pure[F])
          case Right(value) =>
            marshalResponse(token.map(_ => LOG_IN_MESSAGE).pure[F]).map(x =>
              x.putHeaders(Header(LOGIN_HEADER_TOKEN, value))
            )
        }
      } yield res
    }

    def logOut(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "portal" / "user" / "logOut" =>
      for {
        headerExists <- req.headers.get(CaseInsensitiveString(LOGIN_HEADER_TOKEN)).isDefined.pure[F]
        answ          = userService.logOut(headerExists)
        res          <- marshalResponse(answ).map(x => x.filterHeaders(x => x.name != CaseInsensitiveString(LOGIN_HEADER_TOKEN)))
      } yield res
    }

    logIn() <+> logOut()
  }

}
