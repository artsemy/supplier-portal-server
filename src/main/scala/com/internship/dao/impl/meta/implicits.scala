package com.internship.dao.impl.meta

import com.internship.domain.{ProductStatus, Role}
import com.internship.util.CaseConversionUtil._
import doobie._

object implicits {

  implicit val roleMeta: Meta[Role] = Meta[String].timap(stringToRole)(_.toString.toUpperCase)

  private def stringToRole(s: String): Role = {
    s match {
      case "CLIENT"  => Role.Client
      case "MANAGER" => Role.Manager
      case "COURIER" => Role.Courier
//      case _ => Role.Client //should we check that?
    }
  }

  implicit val productStatusMeta: Meta[ProductStatus] =
    Meta[String].timap(stringToProductStatus)(x => camelToSnake(x.toString).toUpperCase())

  private def stringToProductStatus(s: String): ProductStatus = {
    s match {
      case "IN_PROCESSING" => ProductStatus.InProcessing
      case "AVAILABLE"     => ProductStatus.Available
      case "NOT_AVAILABLE" => ProductStatus.NotAvailable
      //      case _ => ProductStatus.InProcessing //should we check that?
    }
  }

}
