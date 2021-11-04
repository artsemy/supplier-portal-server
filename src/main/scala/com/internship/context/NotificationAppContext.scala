package com.internship.context

import cats.implicits._
import cats.effect.{Async, ContextShift, Resource, Timer}
import org.http4s.HttpApp
import org.http4s.implicits._
import com.internship.conf.app.AppConf
import com.internship.conf.db.transactor
import com.internship.dao.NotificationDAO
import com.internship.router.NotificationRouter
import com.internship.service.NotificationService
import com.emarsys.scheduler.Schedule
import com.emarsys.scheduler.syntax._

import scala.concurrent.duration._

object NotificationAppContext {

  def setUp[F[_]: ContextShift: Async: Timer](conf: AppConf): Resource[F, HttpApp[F]] = for {

    tx <- transactor[F](conf.db)

    notificationDAO     = NotificationDAO.of[F](tx)
    notificationService = NotificationService.of(notificationDAO)

    _ <- Resource.eval(for {
      x <- notificationService.sendMessageCategoryUpdate()
      y <- notificationService.sendMessageSupplierUpdate()
    } yield (x + y)) runOn Schedule.spaced(10.seconds)

    httpApp = (NotificationRouter.routes[F]()).orNotFound
  } yield httpApp

}
