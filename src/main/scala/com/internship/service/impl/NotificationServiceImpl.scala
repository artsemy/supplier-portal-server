package com.internship.service.impl

import cats.implicits._
import cats.effect.Sync
import com.internship.constant.ConstantStrings.PreString
import com.internship.dao.NotificationDAO
import com.internship.domain.{NotificationCategory, NotificationSupplier}
import com.internship.service.NotificationService
import com.internship.util.EmailSender.sendEmail
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import java.time.LocalDate

class NotificationServiceImpl[F[_]: Sync](notificationDAO: NotificationDAO[F]) extends NotificationService[F] {

  implicit def unsafeLogger = Slf4jLogger.getLogger[F]

  override def sendMessageCategoryUpdate(): F[Int] = for {
    _       <- Logger[F].info(s"$PreString message category: try")
    today    = LocalDate.parse("2021-10-10") //for tests, final - LocalDate.now()
    resList <- notificationDAO.getNewProductWithCategory(today)
    _       <- emailCategory(resList)
    _       <- Logger[F].info(s"$PreString message category: finish")
  } yield resList.size

  override def sendMessageSupplierUpdate(): F[Int] = for {
    _       <- Logger[F].info(s"$PreString message supplier: try")
    today    = LocalDate.parse("2021-10-10") //for tests, final - LocalDate.now()
    resList <- notificationDAO.getNewProductWithSupplier(today)
    _       <- emailSupplier(resList)
    _       <- Logger[F].info(s"$PreString message supplier: finish")
  } yield resList.size

  private def emailCategory(list: List[NotificationCategory]): F[Unit] = for {
    _ <- Logger[F].info(s"$PreString email category: try")
    _  = list.foreach(x => sendEmail(to = x.userEmail, subject = "category " + x.categoryId, text = x.productName))
    _ <- Logger[F].info(s"$PreString email category: finish")
  } yield ()

  private def emailSupplier(list: List[NotificationSupplier]): F[Unit] = for {
    _ <- Logger[F].info(s"$PreString email supplier: try")
    _  = list.foreach(x => sendEmail(to = x.userEmail, subject = "supplier " + x.supplierId, text = x.productName))
    _ <- Logger[F].info(s"$PreString email supplier: finish")
  } yield ()

}
