package com.internship.dao.impl.meta

import com.internship.domain.Role
import doobie.Meta

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

}
