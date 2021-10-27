package com.internship.service.search

import com.internship.dto.{Pair, SearchDto, Triple}

object SearchParsing {

  def parse(searchDto: SearchDto): String = {
    val s1  = parseExact(searchDto.exact)
    val s2  = parsePeriod(searchDto.period)
    val s3  = parseCategory(searchDto.category)
    val res = buildLine(s1, s2, s3)
    res
  }

  private def parseExact(list: List[Pair]): String = {
    if (list.nonEmpty) {
      val h = list.head
      val s = h.typ + " LIKE '%" + h.value + "%'"
      if (list.tail.nonEmpty) s + " AND " + parseExact(list.tail)
      else s
    } else ""
  }

  private def parsePeriod(list: List[Triple]): String = {
    if (list.nonEmpty) {
      val h = list.head
      val s = h.typ + " BETWEEN '" + h.start + "' AND '" + h.end + "'"
      if (list.tail.nonEmpty) s + " AND " + parsePeriod(list.tail)
      else s
    } else ""
  }

  private def parseCategory(list: List[Int]): String = {
    if (list.nonEmpty) {
      "category_id IN (" + list.map(x => x.toString).reduce(_ + ", " + _) + ")"
    } else ""
  }

  private def buildLine(s1: String, s2: String, s3: String): String = {
    (s1 :: s2 :: s3 :: Nil).filter(_.nonEmpty).reduce(_ + " AND " + _)
  }

}
