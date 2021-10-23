package com.internship.error

import com.internship.domain.dto.ProductDto

trait ProductError extends SupplierPortalError

object ProductError {

  final case class InvalidProductDto(productDto: ProductDto) extends ProductError {
    override def message: String = s"bad product input: $productDto"
  }

  final case class InvalidProductId(id: String) extends ProductError {
    override def message: String = s"bad product id: $id"
  }

}
