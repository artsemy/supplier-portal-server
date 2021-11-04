package com.internship.service.impl

import cats.Monad
import cats.effect.Sync
import cats.implicits._
import com.internship.constant.ConstantStrings.preString
import com.internship.dao.SubscriptionDAO
import com.internship.error.SubscriptionError
import com.internship.service.SubscriptionService
import io.chrisdavenport.log4cats.Logger
import com.internship.util.TraverseEitherTupleUtil._
import com.internship.service.validation.SubscriptionValidator._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

case class SubscriptionServiceImpl[F[_]: Monad: Sync](subscriptionDAO: SubscriptionDAO[F])
  extends SubscriptionService[F] {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  override def addSubscriptionCategory(userId: String, categoryId: String): F[Either[SubscriptionError, Int]] = for {
    _ <- Logger[F].info(s"$preString subs service add category: try")
    res <- traverseTwoTypes(validateUserId(userId), validateCategoryId(categoryId))
      .traverse { case (uId, cId) => subscriptionDAO.addSubscriptionCategory(uId, cId) }
    _ <- Logger[F].info(s"$preString subs service add category: finish")
  } yield res

  override def removeSubscriptionCategory(userId: String, categoryId: String): F[Either[SubscriptionError, Int]] = for {
    _ <- Logger[F].info(s"$preString subs service remove category: try")
    res <- traverseTwoTypes(validateUserId(userId), validateCategoryId(categoryId))
      .traverse { case (uId, cId) => subscriptionDAO.removeSubscriptionCategory(uId, cId) }
    _ <- Logger[F].info(s"$preString subs service remove category: finish")
  } yield res

  override def readAllSubscriptionCategory(userId: String): F[Either[SubscriptionError, Map[Long, String]]] = for {
    _ <- Logger[F].info(s"$preString subs service read all category: try")
    res <- validateUserId(userId)
      .traverse(uId => subscriptionDAO.readAllSubscriptionCategory(uId))
    _ <- Logger[F].info(s"$preString subs service read all category: finish")
  } yield res

  override def addSubscriptionSupplier(userId: String, supplierId: String): F[Either[SubscriptionError, Int]] = for {
    _ <- Logger[F].info(s"$preString subs service add supplier: try")
    res <- traverseTwoTypes(validateUserId(userId), validateSupplierId(supplierId))
      .traverse { case (uId, sId) => subscriptionDAO.addSubscriptionSupplier(uId, sId) }
    _ <- Logger[F].info(s"$preString subs service add supplier: finish")
  } yield res

  override def removeSubscriptionSupplier(userId: String, supplierId: String): F[Either[SubscriptionError, Int]] = for {
    _ <- Logger[F].info(s"$preString subs service remove supplier: try")
    res <- traverseTwoTypes(validateUserId(userId), validateSupplierId(supplierId))
      .traverse { case (uId, sId) => subscriptionDAO.removeSubscriptionSupplier(uId, sId) }
    _ <- Logger[F].info(s"$preString subs service remove supplier: finish")
  } yield res

  override def readAllSubscriptionSupplier(userId: String): F[Either[SubscriptionError, Map[Long, String]]] = for {
    _ <- Logger[F].info(s"$preString subs service read all supplier: try")
    res <- validateUserId(userId)
      .traverse(uId => subscriptionDAO.readAllSubscriptionSupplier(uId))
    _ <- Logger[F].info(s"$preString subs service read all supplier: finish")
  } yield res

}
