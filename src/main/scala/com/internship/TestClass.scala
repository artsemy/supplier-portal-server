package com.internship

import cats.effect.IO
import cats.implicits._

import java.util.concurrent.TimeUnit
import monix.execution.Scheduler.{global => scheduler}
import scala.concurrent.duration._

object TestClass {

  def main(args: Array[String]): Unit = {

    scheduler.scheduleWithFixedDelay(3.seconds, 5.seconds) {
      println("Fixed delay task")
    }

    Thread.sleep(20000)
  }

}
