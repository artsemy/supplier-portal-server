package com.internship.service

import cats.effect.Sync
import com.internship.domain.dto.ProductDto
import com.internship.service.impl.ProductServiceImpl
import com.internship.dao.ProductDAO
import com.internship.error.ProductError

trait ProductService[F[_]] {

  def create(productDto: ProductDto): F[Either[ProductError, Int]]
  def read(productId:    String):     F[Either[ProductError, ProductDto]]
  def update(productDto: ProductDto): F[Either[ProductError, Int]]
  def delete(productId:  String):     F[Either[ProductError, Int]]

}

object ProductService {
  def of[F[_]: Sync](productDAO: ProductDAO[F]): ProductService[F] = new ProductServiceImpl[F](productDAO)
}
