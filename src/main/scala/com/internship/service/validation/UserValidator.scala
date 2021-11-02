package com.internship.service.validation

import com.internship.error.UserError

object UserValidator {

  trait UserValidationError extends UserError

  object UserValidationError {
    final case object UserIdFormat extends UserValidationError {
      override def message: String = "user id format"
    }
  }

  import UserValidationError._

  def validateUserId(id: String): Either[UserValidationError, Long] = {
    id.toLongOption.toRight(UserIdFormat)
  }

}
