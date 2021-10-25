package com.internship.context

import cats.effect.{Async, ContextShift, Resource}
import org.http4s.HttpApp
import org.http4s.implicits._
import com.internship.conf.app.AppConf
import com.internship.conf.db.{migrator, transactor}
import com.internship.dao.{ProductDAO, UserDAO}
import com.internship.router.{ProductRoutes, UserRoutes}
import com.internship.service.{ProductService, UserService}

import cats.implicits._

object AppContext {

  def setUp[F[_]: ContextShift: Async](conf: AppConf): Resource[F, HttpApp[F]] = for {
    tx <- transactor[F](conf.db)

    migrator <- Resource.eval(migrator[F](conf.db))
    _        <- Resource.eval(migrator.migrate())

    userDao     = UserDAO.of[F](tx)
    userService = UserService.of(userDao)

    productDao     = ProductDAO.of[F](tx)
    productService = ProductService.of(productDao)

    httpApp = (UserRoutes.routes[F](userService) <+> ProductRoutes.routes[F](productService, userService)).orNotFound
  } yield httpApp

}
