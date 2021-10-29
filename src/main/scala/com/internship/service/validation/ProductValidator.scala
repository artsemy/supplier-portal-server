package com.internship.service.validation

import com.internship.dto.{ProductDto, SmartSearchDto}
import com.internship.domain.{Product, ProductStatus}
import com.internship.error.ProductError

import java.time.LocalDate
import scala.util.Try

object ProductValidator {

  trait ProductValidationError extends ProductError {}

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

    final case object ProductCriteriaFormat extends ProductValidationError {
      override def message: String = "product criteria format"
    }

    final case class ProductSomeValidationError(error: String) extends ProductValidationError {
      override def message: String = s"er: $error"
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
    if (name.matches("[A-Z][a-z]{2,10}"))
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
    if (description.matches("[A-Z][a-z]{2,10}"))
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
      case "IN_PROCESSING" => Right(ProductStatus.InProcessing)
      case "AVAILABLE"     => Right(ProductStatus.Available)
      case "NOT_AVAILABLE" => Right(ProductStatus.NotAvailable)
      case _               => Left(ProductStatusFormat)
    }
  }

  def validateProductId(productId: String): Either[ProductValidationError, Long] = {
    Try(productId.toLong).toEither.left.map(_ => ProductIdFormat)
  }

  def validateCriteriaType(criteriaType: String): Either[ProductValidationError, Int] = {
    criteriaType match {
      case "name"             => Right(1)
      case "publication_date" => Right(2)
      case "update_date"      => Right(3)
      case "description"      => Right(4)
      case "price"            => Right(5)
      case "supplier_id"      => Right(6)
      case "product_status"   => Right(7)
      case _                  => Left(ProductCriteriaFormat)
    }
  }

  def validateSmartSearchDto(smartSearchDto: SmartSearchDto): Either[ProductValidationError, SmartSearchDto] = {
    //fix
    val res: Either[ProductValidationError, SmartSearchDto] = Right(smartSearchDto)
    res
  }

}
