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
import io.chrisdavenport.log4cats.Logger
//import org.typelevel.log4cats.Logger

class ProductServiceImpl[F[_]: Monad: Logger](productDAO: ProductDAO[F]) extends ProductService[F] {

  override def create(productDto: ProductDto, userTokenDto: UserTokenDto): F[Either[ProductError, Int]] = for {
    _ <- Logger[F].info("product service create: try")
    res <-
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
    _ <- Logger[F].info("product service create: finish")
  } yield res

  override def read(productId: String, userTokenDto: UserTokenDto): F[Either[ProductError, Option[ProductDto]]] = for {
    _ <- Logger[F].info("product service read: try")
    res <-
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
    _ <- Logger[F].info("product service read: finish")
  } yield res

  override def update(
    productId:    String,
    productDto:   ProductDto,
    userTokenDto: UserTokenDto
  ): F[Either[ProductError, Int]] = for {
    _ <- Logger[F].info("product service update: try")
    res <-
      if (userTokenDto.role == Role.Manager.toString) {
        val validId = ProductValidator.validateProductId(productId).getOrElse(0L)
        ProductValidator
          .validate(productDto)
          .traverse(product => productDAO.update(validId, product))
      } else {
        val roleError: Either[ProductError, Int] = Left(RoleNotMatch())
        roleError.pure[F]
      }
    _ <- Logger[F].info("product service update: finish")
  } yield res

  override def delete(productId: String, userTokenDto: UserTokenDto): F[Either[ProductError, Int]] = for {
    _ <- Logger[F].info("product service delete: try")
    res <-
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
    _ <- Logger[F].info("product service delete: finish")
  } yield res

  override def readAll(userTokenDto: UserTokenDto): F[Either[ProductError, Map[Long, ProductDto]]] = for {
    _            <- Logger[F].info("product service readAll: try")
    initMap      <- productDAO.readAll()
    convertedMap <- initMap.map { case (l, product) => (l, convertProductToDto(product)) }.pure[F]
    res: Either[ProductError, Map[Long, ProductDto]] = convertedMap.asRight
    _ <- Logger[F].info("product service readAll: finish")
  } yield res

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

  override def smartSearch(smartSearchDto: SmartSearchDto): F[Either[ProductError, Map[Long, ProductDto]]] = for {
    _ <- Logger[F].info("product service search: try")
    res <- ProductValidator
      .validateSmartSearchDto(smartSearchDto)
      .traverse { dto => productDAO.smartSearch(dto) }
      .map(x => x.map(y => y.map { case (l, product) => (l, convertProductToDto(product)) }))
    _ <- Logger[F].info("product service search: finish")
  } yield res

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
