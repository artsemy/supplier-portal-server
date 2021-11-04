package com.internship.context

import cats.effect.{Async, ContextShift, Resource}
import org.http4s.HttpApp
import org.http4s.implicits._
import com.internship.conf.app.AppConf
import com.internship.conf.db.{migrator, transactor}
import com.internship.dao.{OrderDAO, ProductDAO, SubscriptionDAO, UserDAO}
import com.internship.router.{OrderRoutes, ProductRoutes, SubscriptionRouter, UserRoutes}
import com.internship.service.{OrderService, ProductService, SubscriptionService, UserService}
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

    orderDao     = OrderDAO.of[F](tx)
    orderService = OrderService.of(orderDao)

    subscriptionDAO     = SubscriptionDAO.of[F](tx)
    subscriptionService = SubscriptionService.of(subscriptionDAO)

    httpApp = (UserRoutes.routes[F](userService) <+> ProductRoutes.routes[F](productService, userService) <+>
      OrderRoutes.routes[F](orderService) <+> SubscriptionRouter.routes[F](subscriptionService)).orNotFound
  } yield httpApp

}
