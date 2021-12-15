package com.internship.modules

import cats.effect.{Concurrent, Sync}
import com.internship.auth.{Crypto, Tokens}
import com.internship.conf.app.{AppConf, TokenExpiration}
import com.internship.dao.UserDAO
import com.internship.router.users.{ClientJwtAuth, ClientUser, CourierJwtAuth, CourierUser, ManagerJwtAuth, ManagerUser}
import com.internship.service.UsersAuth
import dev.profunktor.auth.jwt.JwtAuth
import dev.profunktor.redis4cats.RedisCommands
import pdi.jwt.JwtAlgorithm

import scala.concurrent.duration.DurationInt

sealed abstract class Security[F[_]](
  val managerAuth:    UsersAuth[F, ManagerUser],
  val clientAuth:     UsersAuth[F, ClientUser],
  val courierAuth:    UsersAuth[F, CourierUser],
  val managerJwtAuth: ManagerJwtAuth,
  val clientJwtAuth:  ClientJwtAuth,
  val courierJwtAuth: CourierJwtAuth
)

object Security {
  def of[F[_]: Sync: Concurrent](
    conf:    AppConf,
    redis:   RedisCommands[F, String, String],
    userDAO: UserDAO[F]
  ): Security[F] = {

    val tokens = Tokens.make[F](conf.tokenConf)
    val crypto = Crypto.make[F](conf.tokenConf.passwordSalt)

    val managerAuth = UsersAuth.manager[F](userDAO) //fix later
    val clientAuth  = UsersAuth.client[F](redis)
    val courierAuth = UsersAuth.courier[F](redis)

    val managerJwtAuth: ManagerJwtAuth = ManagerJwtAuth(
      JwtAuth.hmac(conf.tokenConf.jwtAccessManagerTokenKeyConfig.value, JwtAlgorithm.HS256)
    )
    val clientJwtAuth: ClientJwtAuth = ClientJwtAuth(
      JwtAuth.hmac(conf.tokenConf.jwtAccessClientTokenKeyConfig.value, JwtAlgorithm.HS256)
    )
    val courierJwtAuth: CourierJwtAuth = CourierJwtAuth(
      JwtAuth.hmac(conf.tokenConf.jwtAccessCourierTokenKeyConfig.value, JwtAlgorithm.HS256)
    )

    new Security[F](managerAuth, clientAuth, courierAuth, managerJwtAuth, clientJwtAuth, courierJwtAuth) {}
  }
}
