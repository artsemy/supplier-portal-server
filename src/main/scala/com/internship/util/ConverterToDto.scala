package com.internship.util

import com.internship.domain.{Order, Product}
import com.internship.dto.{OrderDto, ProductDto}

object ConverterToDto {

  def convertProductToDto(product: Product): ProductDto = {
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

  def convertOrderToDto(order: Order): OrderDto = {
    OrderDto(
      order.ownerId.toString,
      order.courierId.toString,
      order.orderStatus.toString,
      order.address
    )
  }

}
