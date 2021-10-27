package com.internship

import cats.effect._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder
import io.circe.config.parser

import com.internship.conf.app._
import com.internship.context.AppContext

import scala.concurrent.ExecutionContext

object SupplierPortalServer extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    serverResource[IO]
      .use(_ => IO.never)
      .as(ExitCode.Success)

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
curl -XGET "localhost:9000/portal/logIn" -H "Content-Type: application/json" -d "{\"login\": \"arty5\", \"password\": \"123\"}"
curl "localhost:9000/portal/logOut" -H "loginToken: $token$"

//token arty5 - manager
create
curl -XPOST "localhost:9000/portal/product/create" -H "loginToken: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI" -H "Content-Type: application/json" -d "{\"name\": \"Pccc\", \"publicationDate\": \"2010-10-10\", \"updateDate\": \"2010-10-10\", \"description\": \"Descr\", \"price\": \"123.00\", \"supplierId\": \"2\", \"productStatus\": \"AVAILABLE\"}"
read one by id
curl "localhost:9000/portal/product/read/7" -H "loginToken: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI"
update
curl -XPOST "localhost:9000/portal/product/update/7" -H "loginToken: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI" -H "Content-Type: application/json" -d "{\"name\": \"Pcccaa\", \"publicationDate\": \"2010-10-10\", \"updateDate\": \"2010-10-10\", \"description\": \"Description\", \"price\": \"321.00\", \"supplierId\": \"2\", \"productStatus\": \"NOT_AVAILABLE\"}"
delete
curl -XPOST "localhost:9000/portal/product/delete/7" -H "loginToken: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI"
readAll
curl "localhost:9000/portal/product/read_all" -H "loginToken: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI"
searchBy
curl "localhost:9000/portal/product/search/name/PC" -H "loginToken: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI"
curl "localhost:9000/portal/product/search/supplier_id/2" -H "loginToken: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI"
search
curl -XGET "localhost:9000/portal/product/search" -H "loginToken: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6ImFydHk1Iiwicm9sZSI6Ik1hbmFnZXIifQ.OpC6WDPMXTyPyLZd-M3cAZk_nXdfLStGQx_sWCnIJLI" -H "Content-Type: application/json" -d "{\"exact\":[{\"typ\":\"name\",\"value\":\"PC\"},{\"typ\":\"description\",\"value\":\"fast\"}],\"period\":[{\"typ\":\"publication_date\",\"start\":\"2020-10-10\",\"end\":\"2022-10-10\"},{\"typ\":\"update_date\",\"start\":\"2020-10-10\",\"end\":\"2022-10-10\"}],\"category\":[1, 2]}"
 */
