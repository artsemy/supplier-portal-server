package com.internship.service.impl

import cats.Monad
import cats.effect.Sync
import cats.implicits._
import com.internship.dao.UserDAO
import com.internship.error.UserError
import com.internship.error.UserError._
import com.internship.dto.AuthDto
import com.internship.service.UserService
import com.internship.domain._
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger
import com.internship.constant.ConstantStrings._
import com.internship.util.PasswordUtil._

class UserServiceImpl[F[_]: Monad: Sync](userDAO: UserDAO[F]) extends UserService[F] {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  override def logIn(authDto: AuthDto): F[Either[UserError, FullUser]] = {
    for {
      _    <- Logger[F].info(s"$PreString user service logIn: try to login")
      user <- getUser(authDto)
      _    <- Logger[F].info(s"$PreString user service logIn: answer generated")
    } yield user
  }

  override def logOut(tokenExists: Boolean): F[Either[UserError, String]] = for {
    _      <- Logger[F].info(s"$PreString user service logOut: try to logout")
    message = if (tokenExists) Right(LogOutMessage) else Left(UserCantLogOut())
    _      <- Logger[F].info(s"$PreString user service logOut: answer generated")
  } yield message

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

}
