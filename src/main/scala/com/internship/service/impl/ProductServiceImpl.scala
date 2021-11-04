package com.internship.service.impl

import cats.Monad
import cats.effect.Sync
import cats.implicits._
import com.internship.constant.ConstantStrings.preString
import com.internship.dto.{ProductDto, SmartSearchDto}
import com.internship.service.ProductService
import com.internship.dao.ProductDAO
import com.internship.error.ProductError
import com.internship.service.validation.ProductValidator
import com.internship.util.TraverseEitherTupleUtil.traverseTwoTypes
import com.internship.util.ConverterToDto.convertProductToDto
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
//import org.typelevel.log4cats.Logger

class ProductServiceImpl[F[_]: Monad: Sync](productDAO: ProductDAO[F]) extends ProductService[F] {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  override def create(productDto: ProductDto): F[Either[ProductError, Int]] = for {
    _   <- Logger[F].info(s"$preString product service create: try")
    res <- ProductValidator.validate(productDto).traverse { case product => productDAO.create(product) }
    _   <- Logger[F].info(s"$preString product service create: finish")
  } yield res

  override def read(productId: String): F[Either[ProductError, Option[ProductDto]]] = for {
    _ <- Logger[F].info(s"$preString product service read: try")
    res <- ProductValidator
      .validateProductId(productId)
      .traverse { id => productDAO.read(id) }
      .map(x => x.map(y => y.map(z => convertProductToDto(z))))
    _ <- Logger[F].info(s"$preString product service read: finish")
  } yield res

  override def update(productId: String, productDto: ProductDto): F[Either[ProductError, Int]] = for {
    _ <- Logger[F].info(s"$preString product service update: try")
    res <- traverseTwoTypes(ProductValidator.validateProductId(productId), ProductValidator.validate(productDto))
      .traverse { case (l, product) => productDAO.update(l, product) }
    _ <- Logger[F].info(s"$preString product service update: try")
  } yield res

  override def delete(productId: String): F[Either[ProductError, Int]] = for {
    _   <- Logger[F].info(s"$preString product service delete: try")
    res <- ProductValidator.validateProductId(productId).traverse { id => productDAO.delete(id) }
    _   <- Logger[F].info(s"$preString product service delete: finish")
  } yield res

  override def readAll(): F[Either[ProductError, Map[Long, ProductDto]]] = for {
    _            <- Logger[F].info("product service readAll: try")
    initMap      <- productDAO.readAll()
    convertedMap <- initMap.map { case (l, product) => (l, convertProductToDto(product)) }.pure[F]
    res: Either[ProductError, Map[Long, ProductDto]] = convertedMap.asRight
    _ <- Logger[F].info(s"$preString product service readAll: finish")
  } yield res

  override def smartSearch(smartSearchDto: SmartSearchDto): F[Either[ProductError, Map[Long, ProductDto]]] = for {
    _ <- Logger[F].info(s"$preString product service search: try")
    res <- ProductValidator
      .validateSmartSearchDto(smartSearchDto)
      .traverse { dto => productDAO.smartSearch(dto) }
      .map(x => x.map(y => y.map { case (l, product) => (l, convertProductToDto(product)) }))
    _ <- Logger[F].info(s"$preString product service search: finish")
  } yield res

}
