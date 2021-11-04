package com.internship

import cats.effect._
import io.circe.config.parser
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext
import com.internship.conf.app._
import com.internship.context.AppContext

object SupplierPortalServer extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    serverResource[IO]
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

  private def serverResource[F[_]: ContextShift: ConcurrentEffect: Timer]: Resource[F, Server[F]] = for {
    conf    <- Resource.eval(parser.decodePathF[F, AppConf]("app"))
    httpApp <- AppContext.setUp[F](conf)

    server <- BlazeServerBuilder[F](ExecutionContext.global)
      .bindHttp(conf.server.port, conf.server.host)
      .withHttpApp(httpApp)
      .resource

  } yield server
}

/*
//login & logout
curl -XGET "localhost:9000/portal/user/logIn" -H "Content-Type: application/json" -d "{\"login\": \"arty1\", \"password\": \"1234\"}"
curl "localhost:9000/portal/user/logOut" -H "loginToken: $token$"
curl -XPOST "localhost:9000/portal/user/subscribe_category/1/1"
curl -XPOST "localhost:9000/portal/user/subscribe_supplier/1/1"
---------------------------------------------------------------------------
//token arty5 - manager //product
create
curl -XPOST "localhost:9000/portal/product/create" -H "loginToken: yJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI" -H "Content-Type: application/json" -d "{\"name\": \"Pccc\", \"publicationDate\": \"2010-10-10\", \"updateDate\": \"2010-10-10\", \"description\": \"Descr\", \"price\": \"123.00\", \"supplierId\": \"2\", \"productStatus\": \"AVAILABLE\"}"
read one by id
curl "localhost:9000/portal/product/read/7" -H "loginToken: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI"
update
curl -XPOST "localhost:9000/portal/product/update/2" -H "loginToken: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI" -H "Content-Type: application/json" -d "{\"name\": \"Pcccaa\", \"publicationDate\": \"2010-10-10\", \"updateDate\": \"2010-10-10\", \"description\": \"Description\", \"price\": \"321.00\", \"supplierId\": \"2\", \"productStatus\": \"NOTAVAILABLE\"}"
delete
curl -XPOST "localhost:9000/portal/product/delete/7" -H "loginToken: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI"
readAll
curl "localhost:9000/portal/product/read_all" -H "loginToken: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI"
smartSearch
curl -XGET "localhost:9000/portal/product/smart_search" -H "Content-Type: application/json" -d "{\"name\": \"PC\", \"pubDatePeriod\": [\"2010-10-10\", \"2022-10-10\"], \"listCategoryId\": [1, 2]}"
---------------------------------------------------------------------------
//order token user1(CLIENT): eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHkxIiwicm9sZSI6IkNsaWVudCJ9.qFO-jV5m-866TDwe-qTkIjQ8uTLjy_6_8IlDwFHVVNw
create
curl -XPOST "localhost:9000/portal/order/create" -H "Content-Type: application/json" -d "{\"ownerId\": \"5\", \"courierId\": \"0\", \"orderStatus\": \"InProcessing\", \"address\": \"address5\"}"
read
curl "localhost:9000/portal/order/read/8"
update
curl -XPOST "localhost:9000/portal/order/update/8" -H "Content-Type: application/json" -d "{\"ownerId\": \"5\", \"courierId\": \"6\", \"orderStatus\": \"Assigned\", \"address\": \"address5\"}"
delete
curl -XPOST "localhost:9000/portal/order/delete/8"

addProduct
curl -XPOST "localhost:9000/portal/order/add_product/4/5/10"
updateProductAmount
curl -XPOST "localhost:9000/portal/order/update_amount/6/100"
readAllProductInOrder
curl "localhost:9000/portal/order/all_product/4"
removeProduct
curl -XPOST "localhost:9000/portal/order/remove_product/4/5"
changeStatus
curl -XPOST "localhost:9000/portal/order/set_status/3/delivered" -H "Content-Type: application/json" -d "{\"ownerId\": \"3\", \"courierId\": \"6\", \"orderStatus\": \"assigned\", \"address\": \"address3\"}"
-------------------------------------------------------------------------------
 */
