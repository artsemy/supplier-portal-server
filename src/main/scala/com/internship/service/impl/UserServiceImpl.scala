package com.internship.service.impl

import cats.implicits._
import cats.Monad
import cats.data.EitherT
import cats.effect.Sync
import cats.implicits._
import com.internship.dao.UserDAO
import com.internship.error.UserError
import com.internship.error.UserError._
import com.internship.dto.{AuthDto, UserTokenDto}
import com.internship.service.UserService
import com.internship.domain._
import com.internship.service.validation.UserValidator
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
//import org.typelevel.log4cats.Logger
import pdi.jwt.{Jwt, JwtAlgorithm}
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._

import com.internship.util.TraverseEitherTupleUtil._
import com.internship.constant.ConstantStrings._
import scala.util.{Failure, Success, Try}

class UserServiceImpl[F[_]: Monad: Sync](userDAO: UserDAO[F]) extends UserService[F] {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  override def logIn(authDto: AuthDto): F[Either[UserError, String]] = {
    for {
      _       <- Logger[F].info(s"$preString service logIn: try to login")
      message <- getOptionUser(authDto).map(op => op.toRight(UserNotFound()).map(_ => LOG_IN_MESSAGE))
      _       <- Logger[F].info(s"$preString service logIn: answer generated")
    } yield message
  }

  override def logOut(tokenExists: Boolean): F[Either[UserError, String]] = for {
    _       <- Logger[F].info(s"$preString service logOut: try to logout")
    message <- (if (tokenExists) Right(LOG_OUT_MESSAGE) else Left(UserCantLogOut())).pure[F]
    _       <- Logger[F].info(s"$preString service logOut: answer generated")
  } yield message

  private def getOptionUser(authDto: AuthDto): F[Option[User]] = for {
    _       <- Logger[F].info(s"$preString service getOptionUser: try")
    pass    <- encodePass(authDto.password)
    optUser <- userDAO.getUser(authDto.login, pass)
    _       <- Logger[F].info(s"$preString service getOptionUser: done")
  } yield optUser

  private def encodePass(password: String): F[String] = for {
    _    <- Logger[F].info(s"$preString service encodePass: try")
    pass <- password.map(x => x).pure[F] //add encryption
    _    <- Logger[F].info(s"$preString service encodePass: done")
  } yield pass

  //token
  def generateToken(authDto: AuthDto): F[String] = for {
    optUser      <- getOptionUser(authDto)
    userTokenDto <- createTokenDto(optUser)
    token        <- createToken(userTokenDto)
  } yield token.getOrElse("")

  private def createTokenDto(optUser: Option[User]): F[Either[UserError, UserTokenDto]] = {
    val res: Either[UserError, UserTokenDto] = optUser match {
      case Some(value) => Right(UserTokenDto().fromUser(value))
      case None        => Left(UserNotFound())
    }
    res.pure[F]
  }

  private def createToken(tokenDto: Either[UserError, UserTokenDto]): F[Either[UserError, String]] = {
    val res = for {
      dto  <- tokenDto
      json  = Json.obj("login" -> Json.fromString(dto.login), "role" -> Json.fromString(dto.role))
      token = Jwt.encode(json.noSpaces, SECRET_WORD, JwtAlgorithm.HS256)
    } yield token
    res.pure[F]
  }

  def decodeToken(token: String): F[Either[UserError, UserTokenDto]] = {
    val t = Try(Jwt.decodeRawAll(token, SECRET_WORD, Seq(JwtAlgorithm.HS256)).get)
    val res: Either[UserError, UserTokenDto] = t match {
      case Failure(_)          => Left(TokenNotFound())
      case Success((_, t2, _)) => Right(decode[UserTokenDto](t2).getOrElse(UserTokenDto()))
    }
    res.pure[F]
  }

}
