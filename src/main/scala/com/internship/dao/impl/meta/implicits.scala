package com.internship.dao.impl.meta

import com.internship.domain.{ProductStatus, Role}
import com.internship.util.CaseConversionUtil._
import doobie.Meta

object implicits {

  implicit val roleMeta: Meta[Role] = Meta[String].timap(stringToRole)(_.toString.toUpperCase)

  private def stringToRole(s: String): Role = {
    s match {
      case "CLIENT"  => Role.Client
      case "MANAGER" => Role.Manager
      case "COURIER" => Role.Courier
    }
  }

  implicit val productStatusMeta: Meta[ProductStatus] =
    Meta[String].timap(s => ProductStatus.withNameInsensitive(snakeToCamel(s.toLowerCase)))(x =>
      normalizedSnakeCase(x.toString)
    )

  def normalizedSnakeCase(str: String): String = {
    val firstChar      = str.charAt(0).toLower
    val remainingChars = str.substring(1)
    val pureCamelCase  = s"$firstChar$remainingChars"

    camelToSnake(pureCamelCase).toUpperCase
  }

}
