package com.internship.service

import cats.effect.Sync
import com.internship.dao.SubscriptionDAO
import com.internship.error.SubscriptionError
import com.internship.service.impl.SubscriptionServiceImpl
import io.chrisdavenport.log4cats.Logger

trait SubscriptionService[F[_]] {
  def addSubscriptionCategory(userId:     String, categoryId: String): F[Either[SubscriptionError, Int]]
  def removeSubscriptionCategory(userId:  String, categoryId: String): F[Either[SubscriptionError, Int]]
  def readAllSubscriptionCategory(userId: String): F[Either[SubscriptionError, Map[Long, String]]]

  def addSubscriptionSupplier(userId:     String, supplierId: String): F[Either[SubscriptionError, Int]]
  def removeSubscriptionSupplier(userId:  String, supplierId: String): F[Either[SubscriptionError, Int]]
  def readAllSubscriptionSupplier(userId: String): F[Either[SubscriptionError, Map[Long, String]]]
}

object SubscriptionService {
  def of[F[_]: Sync: Logger](subscriptionDAO: SubscriptionDAO[F]): SubscriptionService[F] =
    new SubscriptionServiceImpl[F](subscriptionDAO)
}
