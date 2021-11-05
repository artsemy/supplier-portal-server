package com.internship.util

import com.internship.constant.ConstantStrings.SecretWord
import com.internship.domain.User
import com.internship.dto.UserTokenDto
import com.internship.error.TokenError.{TokenFormatError, TokenNoUserError}
import com.internship.error.{TokenError, UserError}
import io.circe.Json
import io.circe.parser.decode
import pdi.jwt.{Jwt, JwtAlgorithm}

import scala.util.{Failure, Success, Try}

object TokenUtil {

  def generateToken(eitherUser: Either[UserError, User]): Either[TokenError, String] = {
    val res = for {
      user <- eitherUser
      token = encodeToken(UserTokenDto().fromUser(user))
    } yield token
    res.left.map(_ => TokenNoUserError)
  }

  private def encodeToken(userTokenDto: UserTokenDto): String = {
    val json  = Json.obj("login" -> Json.fromString(userTokenDto.login), "role" -> Json.fromString(userTokenDto.role))
    val token = Jwt.encode(json.noSpaces, SecretWord, JwtAlgorithm.HS256)
    token
  }

  def decodeToken(token: String): Either[TokenError, UserTokenDto] = {
    val t = Try(Jwt.decodeRawAll(token, SecretWord, Seq(JwtAlgorithm.HS256)).get)
    val res: Either[TokenError, UserTokenDto] = t match {
      case Failure(_)          => Left(TokenFormatError)
      case Success((_, t2, _)) => Right(decode[UserTokenDto](t2).getOrElse(UserTokenDto()))
    }
    res
  }

}
