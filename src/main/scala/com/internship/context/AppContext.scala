package com.internship.context

import cats.effect.{Async, ContextShift, Resource}
import org.http4s.HttpApp
import org.http4s.implicits._

import com.internship.conf.app.AppConf
import com.internship.conf.db.{migrator, transactor}
import com.internship.dao.UserDAO
import com.internship.router.UserRoutes
import com.internship.service.UserService

object AppContext {

  def setUp[F[_]: ContextShift: Async](conf: AppConf): Resource[F, HttpApp[F]] = for {
    tx <- transactor[F](conf.db)

    migrator <- Resource.eval(migrator[F](conf.db))
    _        <- Resource.eval(migrator.migrate())

    userDao     = UserDAO.of[F](tx)
    userService = UserService.of(userDao)
    httpApp     = UserRoutes.routes[F](userService).orNotFound
  } yield httpApp

}
