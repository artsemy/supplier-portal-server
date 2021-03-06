package com.internship.service

import cats.effect.Sync
import com.internship.dto.{ProductDto, SmartSearchDto, UserTokenDto}
import com.internship.service.impl.ProductServiceImpl
import com.internship.dao.ProductDAO
import com.internship.error.ProductError

trait ProductService[F[_]] {

  def create(productDto: ProductDto): F[Either[ProductError, Int]]
  def read(productId:    String): F[Either[ProductError, Option[ProductDto]]]
  def update(productId:  String, productDto: ProductDto): F[Either[ProductError, Int]]
  def delete(productId:  String):     F[Either[ProductError, Int]]
  def readAll(): F[Either[ProductError, Map[Long, ProductDto]]]
  def smartSearch(smartSearchDto: SmartSearchDto): F[Either[ProductError, Map[Long, ProductDto]]]
}

object ProductService {
  def of[F[_]: Sync](productDAO: ProductDAO[F]): ProductService[F] = new ProductServiceImpl[F](productDAO)
}
