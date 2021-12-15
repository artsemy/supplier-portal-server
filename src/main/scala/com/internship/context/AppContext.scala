package com.internship.context

import cats.effect.{Async, Concurrent, ContextShift, Resource}
import com.internship.auth.Tokens
import org.http4s.HttpApp
import org.http4s.implicits._
import dev.profunktor.redis4cats.Redis
import dev.profunktor.redis4cats.effect.Log.Stdout._
import com.internship.conf.app.AppConf
import com.internship.conf.db.{migrator, transactor}
import com.internship.modules.{Repositories, Routers, Security, Services}

object AppContext {
  def setUp[F[_]: ContextShift: Async: Concurrent](conf: AppConf): Resource[F, HttpApp[F]] = for {
    tx <- transactor[F](conf.db)

    migrator <- Resource.eval(migrator[F](conf.db))
    _        <- Resource.eval(migrator.migrate())

    redis <- Redis[F].utf8(conf.redis.url)

    repositories = Repositories.of[F](tx)

    security = Security.of[F](conf, redis, repositories.userDAO)

    tokens = Tokens.make(conf.tokenConf)

    services = Services.of[F](repositories, redis, tokens)

    routes = Routers.of[F](services, security).routes.orNotFound

  } yield routes

}
