package com.internship.error

trait TokenError extends SupplierPortalError

object TokenError {

  case object TokenNoUserError extends TokenError {
    override def message: String = "can't create token for not existed user"
  }

  case object TokenFormatError extends TokenError {
    override def message: String = "token format"
  }

}
