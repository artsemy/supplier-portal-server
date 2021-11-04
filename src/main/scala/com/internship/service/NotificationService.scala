package com.internship.service

import cats.effect.Sync
import com.internship.dao.NotificationDAO
import com.internship.service.impl.NotificationServiceImpl

trait NotificationService[F[_]] {
  def sendMessageCategoryUpdate(): F[Int]
  def sendMessageSupplierUpdate(): F[Int]
}

object NotificationService {
  def of[F[_]: Sync](notificationDAO: NotificationDAO[F]): NotificationService[F] =
    new NotificationServiceImpl[F](notificationDAO)
}
