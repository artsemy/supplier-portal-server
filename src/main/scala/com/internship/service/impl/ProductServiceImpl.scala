package com.internship.service.impl

import cats.Monad
import cats.implicits._
import com.internship.domain.dto.ProductDto
import com.internship.service.ProductService
import com.internship.dao.ProductDAO
import com.internship.domain.Product
import com.internship.error.ProductError
import com.internship.error.ProductError._
import com.internship.service.validation.ProductValidator

class ProductServiceImpl[F[_]: Monad](productDAO: ProductDAO[F]) extends ProductService[F] {

  override def create(productDto: ProductDto): F[Either[ProductError, Int]] = {
    ProductValidator
      .validate(productDto)
      .traverse { case product @ Product(_, _, _, _, _, _, _) =>
        for {
          res <- productDAO.create(product)
        } yield res
      }
  }

  override def read(productId: String): F[Either[ProductError, ProductDto]] = {
//    ProductValidator
//      .validateProductId(productId)
//      .traverse { id =>
//        for {
//          res <- productDAO.read(id)
//        } yield res
//      }
//      .map(_.left.map(_ => InvalidProductId(productId))) //remove
    ???
  }

  override def update(productDto: ProductDto): F[Either[ProductError, Int]] = { //fix, add id field
    ???
  }

  override def delete(productId: String): F[Either[ProductError, Int]] = {
    ProductValidator
      .validateProductId(productId)
      .traverse { id =>
        for {
          res <- productDAO.delete(id)
        } yield res
      }
      .map(_.left.map(_ => InvalidProductId(productId))) //remove
  }
}
