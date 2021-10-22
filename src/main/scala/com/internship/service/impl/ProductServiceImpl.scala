package com.internship.service.impl

import cats.Monad
import com.internship.router.dto.ProductDto
import com.internship.service.ProductService
import com.internship.dao.ProductDAO
import com.internship.error.ProductError

class ProductServiceImpl[F[_]: Monad](productDAO: ProductDAO[F]) extends ProductService[F] {
  override def create(productDto: ProductDto): F[Either[ProductError, Int]] = ???

  override def read(productId: String): F[Either[ProductError, ProductDto]] = ???

  override def update(productDto: ProductDto): F[Either[ProductError, Int]] = ???

  override def delete(productId: String): F[Either[ProductError, Int]] = ???
}
