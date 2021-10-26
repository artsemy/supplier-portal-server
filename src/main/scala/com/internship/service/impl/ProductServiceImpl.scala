package com.internship.service.impl

import cats.Monad
import cats.implicits._
import com.internship.dto.{ProductDto, UserTokenDto}
import com.internship.service.ProductService
import com.internship.dao.ProductDAO
import com.internship.domain.{Product, Role}
import com.internship.error.ProductError
import com.internship.error.ProductError._
import com.internship.service.validation.ProductValidator

import scala.collection.immutable.{AbstractMap, SeqMap, SortedMap}

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
    val resMap       = productDAO.readAll()
    val convertedMap = resMap.map(x => x.map { case (l, product) => (l, convertProductToDto(product)) })
    val res: F[Either[ProductError, Map[Long, ProductDto]]] = convertedMap.map(x => Right(x))
    res
  }

  private def convertProductToDto(product: Product): ProductDto = {
    ProductDto(
      product.name,
      product.publicationDate.toString,
      product.updateDAte.toString,
      product.description,
      product.price,
      product.supplierId.toString,
      product.productStatus.toString
    )
  }

}
