package com.internship.auth

import cats.Monad
import cats.effect.Sync
import cats.syntax.all._
import dev.profunktor.auth.jwt._
import pdi.jwt._
import io.circe.syntax._

import com.internship.conf.app.TokenConf
import com.internship.domain.Role
import com.internship.domain.Role.{Client, Courier, Manager}
import com.internship.util.GenUUID

trait Tokens[F[_]] {
  def create(role: Role): F[JwtToken]
}

object Tokens {
  def make[F[_]: Sync: Monad](config: TokenConf): Tokens[F] =
    new Tokens[F] {
      def create(role: Role): F[JwtToken] =
        for {
          uuid <- GenUUID[F].make
          claim = JwtClaim(uuid.asJson.noSpaces)
          secretKey = role match {
            case Client  => JwtSecretKey(config.jwtAccessClientTokenKeyConfig.value)
            case Courier => JwtSecretKey(config.jwtAccessCourierTokenKeyConfig.value)
            case Manager => JwtSecretKey(config.jwtAccessManagerTokenKeyConfig.value)
          }
          token <- jwtEncode[F](claim, secretKey, JwtAlgorithm.HS256)
        } yield token
    }

}
