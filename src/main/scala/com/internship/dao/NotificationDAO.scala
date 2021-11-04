package com.internship.dao

import cats.effect.Sync
import com.internship.dao.impl.DoobieNotificationDAO
import com.internship.domain.{NotificationCategory, NotificationSupplier}
import doobie.util.transactor.Transactor

import java.time.LocalDate

trait NotificationDAO[F[_]] {
  def getNewProductWithSupplier(today: LocalDate): F[List[NotificationSupplier]]
  def getNewProductWithCategory(today: LocalDate): F[List[NotificationCategory]]
}

object NotificationDAO {
  def of[F[_]: Sync](tx: Transactor[F]): NotificationDAO[F] = new DoobieNotificationDAO[F](tx)
}
