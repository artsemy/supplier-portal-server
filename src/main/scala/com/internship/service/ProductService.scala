package com.internship.service

import cats.effect.Sync
import com.internship.dto.{ProductDto, SmartSearchDto, UserTokenDto}
import com.internship.service.impl.ProductServiceImpl
import com.internship.dao.ProductDAO
import com.internship.error.ProductError
import io.chrisdavenport.log4cats.Logger
//import org.typelevel.log4cats.Logger

trait ProductService[F[_]] {

  def create(productDto:          ProductDto, userTokenDto: UserTokenDto): F[Either[ProductError, Int]]
  def read(productId:             String, userTokenDto:     UserTokenDto): F[Either[ProductError, Option[ProductDto]]]
  def update(productId:           String, productDto:       ProductDto, userTokenDto: UserTokenDto): F[Either[ProductError, Int]]
  def delete(productId:           String, userTokenDto: UserTokenDto): F[Either[ProductError, Int]]
  def readAll(userTokenDto:       UserTokenDto): F[Either[ProductError, Map[Long, ProductDto]]]
  def smartSearch(smartSearchDto: SmartSearchDto):          F[Either[ProductError, Map[Long, ProductDto]]]
}

object ProductService {
  def of[F[_]: Sync: Logger](productDAO: ProductDAO[F]): ProductService[F] = new ProductServiceImpl[F](productDAO)
}
