package com.internship.router

import cats.effect.Sync
import cats.implicits._
import com.internship.constant.ConstantStrings.LOGIN_HEADER_TOKEN
import com.internship.domain.Role
import com.internship.router.MarshalResponse.marshalResponse
import com.internship.dto.{ProductDto, SmartSearchDto, UserTokenDto}
import com.internship.error.RoleError.RoleNotMatch
import com.internship.error.{RoleError, SupplierPortalError, TokenError}
import com.internship.error.TokenError.TokenFormatError
import com.internship.service.ProductService
import com.internship.util.TokenUtil
import org.http4s.{HttpRoutes, Request}
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.util.CaseInsensitiveString
import io.circe.generic.auto._
import io.circe._
import io.circe.generic.JsonCodec
import io.circe.parser._
import io.circe.syntax._
import com.internship.util.TraverseEitherTupleUtil._

object ProductRoutes {

  def routes[F[_]: Sync](productService: ProductService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    def create(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ POST -> Root / "portal" / "product" / "create" =>
      val res = for {
        productDto <- req.as[ProductDto]
        tokenRole  <- getTokenRole(req)
        func       <- productService.create(productDto)
        created     = traverseThreeTypes(tokenRole, checkRole(tokenRole, Role.Manager), func)
      } yield created.map { case (_, _, id) => id }
      marshalResponse(res)
    }

    def read(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "portal" / "product" / "read" / id =>
      val res = for {
        tokenRole <- getTokenRole(req)
        func      <- productService.read(id)
        read       = traverseTwoTypes(tokenRole, func)
      } yield read.map { case (_, productDto) => productDto }
      marshalResponse(res)
    }

    def update(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ POST -> Root / "portal" / "product" / "update" / id =>
      val res = for {
        productDto <- req.as[ProductDto]
        tokenRole  <- getTokenRole(req)
        func       <- productService.update(id, productDto)
        updated     = traverseThreeTypes(tokenRole, checkRole(tokenRole, Role.Manager), func)
      } yield updated.map { case (_, _, id) => id }
      marshalResponse(res)
    }

    def delete(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ POST -> Root / "portal" / "product" / "delete" / id =>
      val res = for {
        tokenRole <- getTokenRole(req)
        func      <- productService.delete(id)
        deleted    = traverseThreeTypes(tokenRole, checkRole(tokenRole, Role.Manager), func)
      } yield deleted.map { case (_, _, id) => id }
      marshalResponse(res)
    }

    def readAll(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "portal" / "product" / "read_all" =>
      val res = for {
        tokenRole <- getTokenRole(req)
        func      <- productService.readAll()
        read       = traverseTwoTypes(tokenRole, func)
      } yield read.map { case (_, resMap) => resMap }
      marshalResponse(res)
    }

    def smartSearch(): HttpRoutes[F] = HttpRoutes.of[F] {
      case req @ GET -> Root / "portal" / "product" / "smart_search" =>
        val res = for {
          smartSearchDto <- req.as[SmartSearchDto]
          tokenRole      <- getTokenRole(req)
          func           <- productService.smartSearch(smartSearchDto)
          search          = traverseTwoTypes(tokenRole, func)
        } yield search.map { case (_, resMap) => resMap }
        marshalResponse(res)
    }

    def checkRole(eithRole: Either[TokenError, Role], role: Role): Either[SupplierPortalError, Int] = {
      eithRole match {
        case Left(value)  => Left(value)
        case Right(value) => if (value == role) Right(1) else Left(RoleNotMatch)
      }
    }

    def getTokenRole(req: Request[F]): F[Either[TokenError, Role]] = for {
      jsonToken <- req.headers.get(CaseInsensitiveString(LOGIN_HEADER_TOKEN)).get.value.pure[F] //error handle???
      token      = TokenUtil.decodeToken(jsonToken).getOrElse(UserTokenDto())
      role       = Role.withNameInsensitiveEither(token.role).left.map(_ => TokenFormatError)
    } yield role

    create() <+> read() <+> update() <+> delete() <+> readAll() <+> smartSearch()
  }

}
