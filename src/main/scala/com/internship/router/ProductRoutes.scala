package com.internship.router

import cats.effect.Sync
import cats.implicits._
import com.internship.router.MarshalResponse.marshalResponse
import com.internship.dto.{ProductDto, UserTokenDto}
import com.internship.service.{ProductService, UserService}
import org.http4s.{HttpRoutes, Request}
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.util.CaseInsensitiveString

object ProductRoutes {

  def routes[F[_]: Sync](productService: ProductService[F], userService: UserService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    val LOGIN_HEADER_TOKEN = "loginToken"

    def create(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ POST -> Root / "portal" / "product" / "create" =>
      val res = for {
        productDto <- req.as[ProductDto]
        token      <- getToken(req)
        created    <- productService.create(productDto, token)
      } yield created
      marshalResponse(res)
    }

    def read(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "portal" / "product" / "read" / id =>
      val res = for {
        token <- getToken(req)
        read  <- productService.read(id, token)
      } yield read
      marshalResponse(res)
    }

    def update(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ POST -> Root / "portal" / "product" / "update" / id =>
      val res = for {
        productDto <- req.as[ProductDto]
        token      <- getToken(req)
        updated    <- productService.update(id, productDto, token)
      } yield updated
      marshalResponse(res)
    }

    def delete(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ POST -> Root / "portal" / "product" / "delete" / id =>
      val res = for {
        token   <- getToken(req)
        deleted <- productService.delete(id, token)
      } yield deleted
      marshalResponse(res)
    }

    def readAll(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "portal" / "product" / "read_all" =>
      val res = for {
        token <- getToken(req)
        read  <- productService.readAll(token)
      } yield read
      marshalResponse(res)
    }

    def searchBy(): HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ GET -> Root / "portal" / "product" / "search" / criteriaType / criteriaValue =>
        val res = for {
          token    <- getToken(req)
          searched <- productService.searchBy(criteriaType, criteriaValue, token)
        } yield searched
        marshalResponse(res)
    }

    def getToken(req: Request[F]): F[UserTokenDto] = for {
      jsonToken    <- req.headers.get(CaseInsensitiveString(LOGIN_HEADER_TOKEN)).get.value.pure[F]
      userTokenDto <- userService.decodeToken(jsonToken)
      token         = userTokenDto.getOrElse(UserTokenDto())
    } yield token

    create() <+> read() <+> update() <+> delete() <+> readAll() <+> searchBy()
  }

}
