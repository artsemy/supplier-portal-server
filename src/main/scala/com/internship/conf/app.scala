package com.internship.conf

import io.circe.generic.JsonCodec

import scala.concurrent.duration.FiniteDuration

object app {

  @JsonCodec
  final case class AppConf(
    server:    ServerConf,
    db:        DbConf,
    redis:     RedisConf,
    tokenConf: TokenConf
  )

  @JsonCodec
  final case class DbConf(
    provider:          String,
    driver:            String,
    url:               String,
    user:              String,
    password:          String,
    migrationLocation: String
  )

  @JsonCodec
  final case class ServerConf(
    host: String,
    port: Int
  )

  @JsonCodec
  final case class RedisConf(
    url: String
  )

  @JsonCodec
  final case class TokenConf(
    jwtAccessClientTokenKeyConfig:  JwtAccessClientTokenKeyConfig,
    jwtAccessCourierTokenKeyConfig: JwtAccessCourierTokenKeyConfig,
    jwtAccessManagerTokenKeyConfig: JwtAccessManagerTokenKeyConfig,
    passwordSalt:                   PasswordSalt,
    expiration:                     Int
  )
  @JsonCodec
  final case class JwtAccessClientTokenKeyConfig(value: String)

  @JsonCodec
  final case class JwtAccessCourierTokenKeyConfig(value: String)

  @JsonCodec
  final case class JwtAccessManagerTokenKeyConfig(value: String)

  @JsonCodec
  final case class PasswordSalt(value: String)

  final case class TokenExpiration(value: FiniteDuration)
}
