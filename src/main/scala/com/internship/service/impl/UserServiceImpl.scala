package com.internship.service.impl

import cats.implicits._
import cats.Monad
import com.internship.dao.UserDAO
import com.internship.error.UserError
import com.internship.error.UserError._
import com.internship.dto.{AuthDto, UserTokenDto}
import com.internship.service.UserService
import com.internship.domain._
import pdi.jwt.{Jwt, JwtAlgorithm}
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._

import scala.util.{Failure, Success, Try}

class UserServiceImpl[F[_]: Monad](authDAO: UserDAO[F]) extends UserService[F] {

  val LOG_OUT_MESSAGE = "logged out"
  val LOG_IN_MESSAGE  = "logged in"
  val SECRET_WORD     = "secretWord"

  override def logIn(authDto: AuthDto): F[Either[UserError, String]] = {
    getOptionUser(authDto).map {
      case Some(_) => Right(LOG_IN_MESSAGE)
      case None    => Left(UserNotFound())
    }
  }

  override def logOut(tokenExists: Boolean): F[Either[UserError, String]] = {
    val res: Either[UserError, String] =
      if (tokenExists)
        Right(LOG_OUT_MESSAGE)
      else
        Left(UserCantLogOut())
    res.pure[F]
  }

  private def getOptionUser(authDto: AuthDto): F[Option[User]] = for {
    pass    <- encodePass(authDto.password)
    optUser <- authDAO.getUser(authDto.login, pass)
  } yield optUser

  private def encodePass(password: String): F[String] = {
    password.map(x => x).pure[F] //add encryption
  }

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
