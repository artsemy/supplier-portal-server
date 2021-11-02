package com.internship.dao

import cats.effect.Sync
import com.internship.dao.impl.DoobieSubscriptionDAO
import doobie.util.transactor.Transactor

trait SubscriptionDAO[F[_]] {
  def addSubscriptionCategory(userId:     Long, categoryId: Long): F[Int]
  def removeSubscriptionCategory(userId:  Long, categoryId: Long): F[Int]
  def readAllSubscriptionCategory(userId: Long): F[Map[Long, String]]

  def addSubscriptionSupplier(userId:     Long, supplierId: Long): F[Int]
  def removeSubscriptionSupplier(userId:  Long, supplierId: Long): F[Int]
  def readAllSubscriptionSupplier(userId: Long): F[Map[Long, String]]
}

object SubscriptionDAO {
  def of[F[_]: Sync](tx: Transactor[F]): DoobieSubscriptionDAO[F] = new DoobieSubscriptionDAO[F](tx)
}
