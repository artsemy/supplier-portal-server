package com.internship.dao.impl

import cats.Functor
import cats.effect.Bracket
import com.internship.dao.NotificationDAO
import com.internship.domain.{NotificationCategory, NotificationSupplier}
import doobie.util.transactor.Transactor
import doobie.implicits._
import doobie.util.fragment.Fragment

import java.time.LocalDate

class DoobieNotificationDAO[F[_]: Functor: Bracket[*[_], Throwable]](tx: Transactor[F]) extends NotificationDAO[F] {

  val selectSupplier = fr"select p.name, u.email, s.supplier_id from " ++
    fr"product p join subscriptions_supplier s on p.supplier_id = s.supplier_id " ++
    fr"join users u on s.users_id = u.id " ++
    fr"where publication_date = "

  val selectCategory = fr"select p.name, u.email, sc.category_id from " ++
    fr"product p join product_category pc on p.id = pc.product_id " ++
    fr"join subscriptions_category sc on pc.category_id = sc.category_id " ++
    fr"join users u on sc.users_id = u.id " ++
    fr"where publication_date = "

  override def getNewProductWithSupplier(today: LocalDate): F[List[NotificationSupplier]] = {
    val fr = selectSupplier ++ Fragment.const(s"'$today'")
    fr.query[NotificationSupplier].to[List].transact(tx)
  }

  override def getNewProductWithCategory(today: LocalDate): F[List[NotificationCategory]] = {
    val fr = selectCategory ++ Fragment.const(s"'$today'")
    fr.query[NotificationCategory].to[List].transact(tx)
  }
}
