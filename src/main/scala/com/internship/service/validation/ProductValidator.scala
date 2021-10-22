package com.internship.service.validation

import com.internship.router.dto.ProductDto
import com.internship.domain.{Product, ProductStatus}

import java.time.LocalDate
import scala.util.Try

object ProductValidator {

  trait ProductValidationError extends Throwable { //SupplierPortalError
    def message: String
  }

  object ProductValidationError {

    final case object ProductNameFormat extends ProductValidationError {
      override def message: String = "product name format"
    }

    final case object ProductDateFormat extends ProductValidationError {
      override def message: String = "product date format"
    }

    final case object ProductPriceFormat extends ProductValidationError {
      override def message: String = "product price format"
    }

    final case object ProductSupplierIdFormat extends ProductValidationError {
      override def message: String = "product supplier id format"
    }

    final case object ProductStatusFormat extends ProductValidationError {
      override def message: String = "product status format"
    }

    final case object ProductIdFormat extends ProductValidationError {
      override def message: String = "product id format"
    }

  }

  import ProductValidationError._

  def validate(productDto: ProductDto): Either[ProductValidationError, Product] = for {
    name            <- validateName(productDto.name)
    publicationDate <- validatePublicationDate(productDto.publicationDate)
    updateDate      <- validateUpdateDate(productDto.publicationDate)
    description     <- validateDescription(productDto.description)
    price           <- validatePrice(productDto.price)
    supplierId      <- validateSupplierId(productDto.supplierId)
    productStatus   <- validateProductStatus(productDto.productStatus)
  } yield Product(name, publicationDate, updateDate, description, price, supplierId, productStatus)

  private def validateName(name: String): Either[ProductValidationError, String] = {
    if (name.matches("[A-Z][a-z\\s]{2,10}"))
      Right(name)
    else
      Left(ProductNameFormat)
  }

  private def validatePublicationDate(localDate: String): Either[ProductValidationError, LocalDate] = {
    Try(LocalDate.parse(localDate)).toEither.left.map(_ => ProductDateFormat)
  }

  private def validateUpdateDate(localDate: String): Either[ProductValidationError, LocalDate] = {
    Try(LocalDate.parse(localDate)).toEither.left.map(_ => ProductDateFormat) //extra check???
  }

  private def validateDescription(description: String): Either[ProductValidationError, String] = {
    if (description.matches("[A-Z][a-z\\s]{2,10}"))
      Right(description)
    else
      Left(ProductNameFormat)
  }

  private def validatePrice(price: String): Either[ProductValidationError, String] = {
    if (price.matches("\\d+.\\d{2}"))
      Right(price)
    else
      Left(ProductPriceFormat)
  }

  private def validateSupplierId(supplierId: String): Either[ProductValidationError, Long] = {
    Try(supplierId.toLong).toEither.left.map(_ => ProductSupplierIdFormat)
  }

  private def validateProductStatus(productStatus: String): Either[ProductValidationError, ProductStatus] = {
    productStatus match {
      case "InProcessing" => Right(ProductStatus.InProcessing)
      case "Available"    => Right(ProductStatus.Available)
      case "NotAvailable" => Right(ProductStatus.NotAvailable)
      case _              => Left(ProductStatusFormat)
    }
  }

  def validateProductId(productId: String): Either[ProductValidationError, Long] = {
    Try(productId.toLong).toEither.left.map(_ => ProductIdFormat)
  }

}
