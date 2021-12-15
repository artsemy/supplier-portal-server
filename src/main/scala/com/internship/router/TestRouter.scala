package com.internship.router

import cats.effect.Sync
import cats.implicits._
import com.internship.service.TestService
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import com.internship.router.users.{ClientUser, CommonUser, CourierUser, ManagerUser}
import dev.profunktor.auth.{jwt, AuthHeaders}
import org.http4s.server._
import com.internship.dto.{AuthDto, OrderDto, ProductDto}
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import com.internship.router.MarshalResponse.marshalResponse

object TestRouter {

  def routes[F[_]: Sync](
    testService:           TestService[F],
    managerAuthMiddleware: AuthMiddleware[F, ManagerUser],
    clientAuthMiddleware:  AuthMiddleware[F, ClientUser],
    courierAuthMiddleware: AuthMiddleware[F, CourierUser]
  ): HttpRoutes[F] = {

    val dsl = new Http4sDsl[F] {}
    import dsl._

    val logIn: HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "test" / "logIn" =>
      for {
        auth <- req.as[AuthDto]
        token = testService.logIn(auth)
        str  <- testService.checkRedis()
        res  <- marshalResponse(token.map(x => x.map(_.value + " " + str)))
      } yield res
    }

    val test: AuthedRoutes[ClientUser, F] = AuthedRoutes.of { case GET -> Root / "test" as _ =>
      Ok("test passed")
    }

    val logOut: AuthedRoutes[ClientUser, F] = AuthedRoutes.of { case ar @ GET -> Root / "test" / "logOut" as user =>
      val token = AuthHeaders.getBearerToken(ar.req)
      val res   = testService.logout(token, user.value.name)
      marshalResponse(res)
    }

    val createProduct: AuthedRoutes[ManagerUser, F] = AuthedRoutes.of {
      case ar @ POST -> Root / "test" / "createProduct" as user =>
//        for {
//          dto    <- ar.req.as[ProductDto]
//          created = testService.createProduct(dto)
//          res    <- marshalResponse(created)
//        } yield res
        Ok("works for manager")
    }

    val changeStatus: AuthedRoutes[CourierUser, F] = AuthedRoutes.of {
      case ar @ POST -> Root / "test" / "changeStatus" / orderId / newStatus as user =>
//        for {
//          orderDto <- ar.req.as[OrderDto]
//          changed   = testService.changeStatus(orderId, orderDto, newStatus)
//          res      <- marshalResponse(changed)
//        } yield res
        Ok("works for courier")
    }

    logIn <+> clientAuthMiddleware(logOut) <+> managerAuthMiddleware(createProduct) <+>
      courierAuthMiddleware(changeStatus) <+> clientAuthMiddleware(test)
  }
}

/*
postman
 */
