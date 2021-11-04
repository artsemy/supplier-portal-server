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
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import com.internship.constant.ConstantStrings._

class UserServiceImpl[F[_]: Monad: Sync](userDAO: UserDAO[F]) extends UserService[F] {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  override def logIn(authDto: AuthDto): F[Either[UserError, User]] = {
    for {
      _    <- Logger[F].info(s"$preString user service logIn: try to login")
      user <- getUser(authDto)
      _    <- Logger[F].info(s"$preString user service logIn: answer generated")
    } yield user
  }

  override def logOut(tokenExists: Boolean): F[Either[UserError, String]] = for {
    _      <- Logger[F].info(s"$preString user service logOut: try to logout")
    message = if (tokenExists) Right(LOG_OUT_MESSAGE) else Left(UserCantLogOut())
    _      <- Logger[F].info(s"$preString user service logOut: answer generated")
  } yield message

  private def getUser(authDto: AuthDto): F[Either[UserError, User]] = for {
    _    <- Logger[F].info(s"$preString user service getUser: try")
    pass <- encodePass(authDto.password)
    user <- userDAO.getUser(authDto.login, pass).map(op => op.toRight(UserNotFound()))
    _    <- Logger[F].info(s"$preString user service getUser: done")
  } yield user

  private def encodePass(password: String): F[String] = for {
    _   <- Logger[F].info(s"$preString user service encodePass: try")
    pass = password.map(x => x) //add encryption
    _   <- Logger[F].info(s"$preString user service encodePass: done")
  } yield pass

}
