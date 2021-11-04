package com.internship.service.impl

import cats.implicits._
import cats.Monad
import cats.effect.{Resource, Sync}
import com.internship.constant.ConstantStrings.preString
import com.internship.dao.NotificationDAO
import com.internship.domain.{NotificationCategory, NotificationSupplier}
import com.internship.service.NotificationService
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

import java.time.LocalDate

class NotificationServiceImpl[F[_]: Monad: Sync](notificationDAO: NotificationDAO[F]) extends NotificationService[F] {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  override def sendMessageCategoryUpdate(): F[Int] = for {
    _       <- Logger[F].info(s"$preString message category: try")
    today    = LocalDate.parse("2021-10-10") //for tests, final - LocalDate.now()
    resList <- notificationDAO.getNewProductWithCategory(today)
    _       <- emailCategory(resList)
    _       <- Logger[F].info(s"$preString message category: finish")
  } yield resList.size

  override def sendMessageSupplierUpdate(): F[Int] = for {
    _       <- Logger[F].info(s"$preString message supplier: try")
    today    = LocalDate.parse("2021-10-10") //for tests, final - LocalDate.now()
    resList <- notificationDAO.getNewProductWithSupplier(today)
    _       <- emailSupplier(resList)
    _       <- Logger[F].info(s"$preString message supplier: finish")
  } yield resList.size

  private def emailCategory(list: List[NotificationCategory]): F[Unit] = for {
    _ <- Logger[F].info(s"$preString email category: try")
    _  = list.foreach(x => println(x)) //real email sending
    _ <- Logger[F].info(s"$preString email category: finish")
  } yield ()

  private def emailSupplier(list: List[NotificationSupplier]): F[Unit] = for {
    _ <- Logger[F].info(s"$preString email supplier: try")
    _  = list.foreach(x => println(x)) //real email sending
    _ <- Logger[F].info(s"$preString email supplier: finish")
  } yield ()

}
