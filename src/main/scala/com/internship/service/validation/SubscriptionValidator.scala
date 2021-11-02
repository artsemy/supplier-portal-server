package com.internship.service.validation

import com.internship.error.SubscriptionError

object SubscriptionValidator {

  trait SubscriptionValidatorError extends SubscriptionError

  object SubscriptionValidatorError {

    case object subscriptionUserIdFormat extends SubscriptionValidatorError {
      override def message: String = "user id format"
    }

    case object subscriptionCategoryIdFormat extends SubscriptionValidatorError {
      override def message: String = "category id format"
    }

    case object subscriptionSupplierIdFormat extends SubscriptionValidatorError {
      override def message: String = "supplier id format"
    }
  }

  import SubscriptionValidatorError._

  def validateUserId(userId: String): Either[SubscriptionValidatorError, Long] = {
    userId.toLongOption.toRight(subscriptionUserIdFormat)
  }

  def validateCategoryId(categoryId: String): Either[SubscriptionValidatorError, Long] = {
    categoryId.toLongOption.toRight(subscriptionCategoryIdFormat)
  }

  def validateSupplierId(supplierId: String): Either[SubscriptionValidatorError, Long] = {
    supplierId.toLongOption.toRight(subscriptionSupplierIdFormat)
  }

}
