package com.internship.context

import cats.effect.{Async, ContextShift, IO, Resource, Sync}
import org.http4s.HttpApp
import org.http4s.implicits._
import com.internship.conf.app.AppConf
import com.internship.conf.db.{migrator, transactor}
import com.internship.dao.{NotificationDAO, OrderDAO, ProductDAO, SubscriptionDAO, UserDAO}
import com.internship.router.{OrderRoutes, ProductRoutes, SubscriptionRouter, UserRoutes}
import com.internship.service.{NotificationService, OrderService, ProductService, SubscriptionService, UserService}
import cats.implicits._
//import com.emarsys.scheduler.Schedule
import monix.eval.Task
import monix.execution.Scheduler.{global => scheduler}

import scala.concurrent.duration._

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
    //add subscription dao, service, rout

    notificationDAO     = NotificationDAO.of[F](tx)
    notificationService = NotificationService.of(notificationDAO)

    _ = scheduler.scheduleWithFixedDelay(1.seconds, 10.seconds) { // not 10.seconds but 24.hours
      notificationService.sendMessageSupplierUpdate()
      notificationService.sendMessageCategoryUpdate()
      println("-" * 200)
    }

    httpApp = (UserRoutes.routes[F](userService) <+> ProductRoutes.routes[F](productService, userService) <+>
      OrderRoutes.routes[F](orderService) <+> SubscriptionRouter.routes[F](subscriptionService)).orNotFound
  } yield httpApp

}
