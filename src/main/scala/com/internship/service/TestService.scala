package com.internship.service

import com.internship.router.auth.UserName
import cats._
import cats.effect.Sync
import cats.syntax.all._
import com.internship.auth.Tokens
import com.internship.constant.ConstantStrings.{LogOutMessage, PreString}
import com.internship.dao.{OrderDAO, ProductDAO, UserDAO}
import com.internship.domain.{FullUser, Order, OrderStatus}
import com.internship.dto.{AuthDto, OrderDto, ProductDto, UserTokenDto}
import com.internship.error.{OrderError, ProductError, UserError}
import com.internship.error.UserError.UserNotFound
import com.internship.modules.Repositories
import com.internship.service.validation.{OrderValidator, ProductValidator}
import com.internship.util.PasswordUtil.encode
import com.internship.util.TraverseEitherTupleUtil.traverseThreeTypes
import dev.profunktor.auth.jwt.JwtToken
import dev.profunktor.redis4cats.RedisCommands
import io.circe.parser.decode
import io.circe.syntax._
import pdi.jwt.JwtClaim
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration
import scala.concurrent.duration.DurationInt

trait TestService[F[_]] {
  def logIn(authDto:            AuthDto): F[Either[UserError, JwtToken]]
  def logout(token:             Option[JwtToken], userName: UserName): F[Either[UserError, String]]
  def createProduct(productDto: ProductDto): F[Either[ProductError, Int]]
  def changeStatus(orderId:     String, orderDto: OrderDto, newStatus: String): F[Either[OrderError, Int]]
  def checkRedis(): F[String]
}

object TestService {

  //removed params
  def of[F[_]: MonadThrow: Monad: Sync](
    redis:        RedisCommands[F, String, String],
    repositories: Repositories[F],
    tokens:       Tokens[F]
  ): TestService[F] =
    new TestService[F] {

      implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]
      val userDAO:    UserDAO[F]    = repositories.userDAO
      val productDAO: ProductDAO[F] = repositories.productDAO
      val orderDAO:   OrderDAO[F]   = repositories.orderDAO

      override def logout(token: Option[JwtToken], userName: UserName): F[Either[UserError, String]] = {
        val res: F[Either[UserError, String]] = token match {
          case None =>
            val p: Either[UserError, String] = Left(UserError.UserCantLogOut())
            p.pure[F]
          case Some(tok) =>
            for {
              _ <- redis.del(tok.value)
              _ <- redis.del(userName.value)
              res: Either[UserError, String] = Right(LogOutMessage)
            } yield res
        }
        res
      }

      override def logIn(authDto: AuthDto): F[Either[UserError, JwtToken]] = {
        for {
          user <- getUser(authDto)
          res <- user match {
            case Left(value) => Left(value).pure[F]
            case Right(user) =>
              val tok = tokens.create(user.role)
              tok.flatMap { t =>
                redis.setEx(t.value, UserTokenDto().fromUser(user).asJson.noSpaces, 30.minutes) *>
                  redis.setEx(user.login, t.value, 30.minutes)
              }
              tok.map(Right(_))
          }
        } yield res
      }

      private def getUser(authDto: AuthDto): F[Either[UserError, FullUser]] = for {
        _    <- Logger[F].info(s"$PreString user service getUser: try")
        pass <- encodePass(authDto.password)
        user <- userDAO.getUser(authDto.login, pass).map(op => op.toRight(UserNotFound()))
        _    <- Logger[F].info(s"$PreString user service getUser: done")
      } yield user

      private def encodePass(password: String): F[String] = for {
        _   <- Logger[F].info(s"$PreString user service encodePass: try")
        pass = encode(password)
        _   <- Logger[F].info(s"$PreString user service encodePass: done")
      } yield pass

      override def createProduct(productDto: ProductDto): F[Either[ProductError, Int]] = for {
        _   <- Logger[F].info(s"$PreString product service create: try")
        res <- ProductValidator.validate(productDto).traverse(product => productDAO.create(product))
        _   <- Logger[F].info(s"$PreString product service create: finish")
      } yield res

      override def changeStatus(orderId: String, orderDto: OrderDto, orderStatus: String): F[Either[OrderError, Int]] =
        for {
          _           <- Logger[F].info(s"$PreString order service change status: try")
          orderId     <- OrderValidator.validateOrderId(orderId).pure[F]
          order       <- OrderValidator.validate(orderDto).pure[F]
          oldStatus   <- order.getOrElse(Order(0, 0, OrderStatus.inProcessing, "")).orderStatus.pure[F]
          newStatus   <- OrderValidator.validateOrderStatus(orderStatus).getOrElse(OrderStatus.inProcessing).pure[F]
          validStatus <- OrderValidator.validateOrderNextStatus(oldStatus, newStatus).pure[F]
          res <- traverseThreeTypes(orderId, order, validStatus).traverse { case (id, order, status) =>
            orderDAO.update(id, order.copy(orderStatus = status))
          }
          _ <- Logger[F].info(s"$PreString order service change status: finish")
        } yield res

      override def checkRedis(): F[String] = {
        redis.set("key", "val")
        redis.get("key").map(x => x.getOrElse("not stored"))
      }

    }

}
