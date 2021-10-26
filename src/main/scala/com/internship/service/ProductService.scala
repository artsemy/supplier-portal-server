package com.internship.service

import cats.effect.Sync
import com.internship.dto.{ProductDto, UserTokenDto}
import com.internship.service.impl.ProductServiceImpl
import com.internship.dao.ProductDAO
import com.internship.error.ProductError

trait ProductService[F[_]] {

  def create(productDto:    ProductDto, userTokenDto: UserTokenDto): F[Either[ProductError, Int]]
  def read(productId:       String, userTokenDto:     UserTokenDto): F[Either[ProductError, Option[ProductDto]]]
  def update(productId:     String, productDto:       ProductDto, userTokenDto: UserTokenDto): F[Either[ProductError, Int]]
  def delete(productId:     String, userTokenDto: UserTokenDto): F[Either[ProductError, Int]]
  def readAll(userTokenDto: UserTokenDto): F[Either[ProductError, Map[Long, ProductDto]]]
  def searchBy(
    criteriaType:  String,
    criteriaValue: String,
    userTokenDto:  UserTokenDto
  ): F[Either[ProductError, Map[Long, ProductDto]]]
}

object ProductService {
  def of[F[_]: Sync](productDAO: ProductDAO[F]): ProductService[F] = new ProductServiceImpl[F](productDAO)
}
