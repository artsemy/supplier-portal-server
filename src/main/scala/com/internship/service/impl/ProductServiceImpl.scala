package com.internship.service.impl

import cats.Monad
import cats.implicits._
import com.internship.dto.{ProductDto, SearchDto, SmartSearchDto, UserTokenDto}
import com.internship.service.ProductService
import com.internship.dao.ProductDAO
import com.internship.domain.{Product, Role}
import com.internship.error.ProductError
import com.internship.error.ProductError._
import com.internship.service.search.SearchParsing
import com.internship.service.validation.ProductValidator

class ProductServiceImpl[F[_]: Monad](productDAO: ProductDAO[F]) extends ProductService[F] {

  override def create(productDto: ProductDto, userTokenDto: UserTokenDto): F[Either[ProductError, Int]] = {
    if (userTokenDto.role == Role.Manager.toString) {
      ProductValidator
        .validate(productDto)
        .traverse { case product @ Product(_, _, _, _, _, _, _) =>
          productDAO.create(product)
        }
    } else {
      val roleError: Either[ProductError, Int] = Left(RoleNotMatch())
      roleError.pure[F]
    }
  }

  override def read(productId: String, userTokenDto: UserTokenDto): F[Either[ProductError, Option[ProductDto]]] = {
    if (
      userTokenDto.role == Role.Manager.toString ||
      userTokenDto.role == Role.Client.toString
    ) {
      ProductValidator
        .validateProductId(productId)
        .traverse { id =>
          for {
            res <- productDAO.read(id)
          } yield res
        }
        .map(x => x.map(y => y.map(z => convertProductToDto(z))))
    } else {
      val roleError: Either[ProductError, Option[ProductDto]] = Left(RoleNotMatch())
      roleError.pure[F]
    }
  }

  override def update(
    productId:    String,
    productDto:   ProductDto,
    userTokenDto: UserTokenDto
  ): F[Either[ProductError, Int]] = {
    if (userTokenDto.role == Role.Manager.toString) {
      val validId = ProductValidator.validateProductId(productId).getOrElse(0L)
      ProductValidator
        .validate(productDto)
        .traverse(product => productDAO.update(validId, product))
    } else {
      val roleError: Either[ProductError, Int] = Left(RoleNotMatch())
      roleError.pure[F]
    }
  }

  override def delete(productId: String, userTokenDto: UserTokenDto): F[Either[ProductError, Int]] = {
    if (userTokenDto.role == Role.Manager.toString) {
      ProductValidator
        .validateProductId(productId)
        .traverse { id =>
          for {
            res <- productDAO.delete(id)
          } yield res
        }
    } else {
      val roleError: Either[ProductError, Int] = Left(RoleNotMatch())
      roleError.pure[F]
    }
  }

  override def readAll(userTokenDto: UserTokenDto): F[Either[ProductError, Map[Long, ProductDto]]] = {
    val initMap      = productDAO.readAll()
    val convertedMap = initMap.map(x => x.map { case (l, product) => (l, convertProductToDto(product)) })
    val res: F[Either[ProductError, Map[Long, ProductDto]]] = convertedMap.map(x => Right(x))
    res
  }

  override def searchBy(
    criteriaType:  String,
    criteriaValue: String,
    userTokenDto:  UserTokenDto
  ): F[Either[ProductError, Map[Long, ProductDto]]] = {
    if (userTokenDto.role == Role.Manager.toString) { //change role
      ProductValidator
        .validateCriteriaType(criteriaType)
        .traverse(criteriaNumber => {
          val initMap    = productDAO.readAll()
          val convertMap = initMap.map(x => x.map { case (l, product) => (l, convertProductToDto(product)) })
          val searchMap = criteriaNumber match {
            case 1 => convertMap.map(x => x.filter { case (l, dto) => dto.name.contains(criteriaValue) })
            case 2 =>
              convertMap.map(x => x.filter { case (l, dto) => dto.publicationDate.contains(criteriaValue) })
            case 3 =>
              convertMap.map(x => x.filter { case (l, dto) => dto.updateDate.contains(criteriaValue) })
            case 4 => convertMap.map(x => x.filter { case (l, dto) => dto.description.contains(criteriaValue) })
            case 5 => convertMap.map(x => x.filter { case (l, dto) => dto.price.contains(criteriaValue) })
            case 6 => convertMap.map(x => x.filter { case (l, dto) => dto.supplierId.contains(criteriaValue) })
            case 7 => convertMap.map(x => x.filter { case (l, dto) => dto.productStatus.contains(criteriaValue) })
          }
          searchMap
        })
    } else {
      val roleError: Either[ProductError, Map[Long, ProductDto]] = Left(RoleNotMatch())
      roleError.pure[F]
    }
  }

  override def search(searchDto: SearchDto): F[Either[ProductError, Map[Long, ProductDto]]] = {
    val line: Either[ProductError, String] = Right(SearchParsing.parse(searchDto))
    line.traverse { l =>
      val r = productDAO.search(l)
      r.map(x => x.map { case (l, product) => (l, convertProductToDto(product)) })
    }
  }

  override def smartSearch(smartSearchDto: SmartSearchDto): F[Either[ProductError, Map[Long, ProductDto]]] = {
    ProductValidator
      .validateSmartSearchDto(smartSearchDto)
      .traverse { dto =>
        productDAO.smartSearch(dto)
      }
      .map(x => x.map(y => y.map { case (l, product) => (l, convertProductToDto(product)) }))
  }

  private def convertProductToDto(product: Product): ProductDto = {
    ProductDto(
      product.name,
      product.publicationDate.toString,
      product.updateDate.toString,
      product.description,
      product.price,
      product.supplierId.toString,
      product.productStatus.toString
    )
  }

}
