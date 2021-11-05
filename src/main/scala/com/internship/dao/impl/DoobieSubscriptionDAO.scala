package com.internship.dao.impl

import cats.Functor
import cats.effect.Bracket
import com.internship.dao.SubscriptionDAO
import doobie.util.transactor.Transactor
import doobie.implicits._
import doobie.util.fragment.Fragment

class DoobieSubscriptionDAO[F[_]: Functor: Bracket[*[_], Throwable]](tx: Transactor[F]) extends SubscriptionDAO[F] {

  override def addSubscriptionCategory(userId: Long, categoryId: Long): F[Int] = {
    val fr = fr"insert into subscriptions_category (users_id, category_id) " ++
      fr"values " ++
      fr"($userId, $categoryId);"
    fr.update.run.transact(tx)
  }

  override def removeSubscriptionCategory(userId: Long, categoryId: Long): F[Int] = {
    val fr = fr"delete from subscriptions_category " ++
      fr"where users_id = $userId and category_id = $categoryId"
    fr.update.run.transact(tx)
  }

  override def readAllSubscriptionCategory(userId: Long): F[Map[Long, String]] = {
    val fr = fr"select s.id, name from subscriptions_category s join category c " ++
      fr"on s.category_id = c.id where " ++
      fr"users_id = $userId"
    fr.query[(Long, String)].toMap.transact(tx)
  }

  override def addSubscriptionSupplier(userId: Long, supplierId: Long): F[Int] = {
    val fr = fr"insert into subscriptions_supplier (users_id, supplier_id) " ++
      fr"values " ++
      fr"($userId, $supplierId);"
    fr.update.run.transact(tx)
  }

  override def removeSubscriptionSupplier(userId: Long, supplierId: Long): F[Int] = {
    val fr = fr"delete from subscriptions_supplier " ++
      fr"where users_id = $userId and supplier_id = $supplierId"
    fr.update.run.transact(tx)
  }

  override def readAllSubscriptionSupplier(userId: Long): F[Map[Long, String]] = {
    val fr = fr"select sub.id, name from subscriptions_supplier sub join supplier sup " ++
      fr"on sub.supplier_id = sup.id where " ++
      fr"users_id = $userId"
    fr.query[(Long, String)].toMap.transact(tx)
  }

}
