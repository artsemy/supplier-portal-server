name := "supplier-portal-server"

version := "0.1"

scalaVersion := "2.13.6"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-Ymacro-annotations",
  "-Xfatal-warnings"
  //  "-Xsource:3"
)

ThisBuild / scalafmtOnCompile := true
val http4s_V = "0.21.7"
val doobie_V = "0.9.0"

libraryDependencies ++= Seq(
  "org.typelevel"        %% "cats-core"            % "2.6.1",
  "org.typelevel"        %% "cats-effect"          % "2.5.2",
  "org.http4s"           %% "http4s-core"          % http4s_V,
  "org.http4s"           %% "http4s-dsl"           % http4s_V,
  "org.http4s"           %% "http4s-blaze-server"  % http4s_V,
  "org.http4s"           %% "http4s-blaze-client"  % http4s_V,
  "org.http4s"           %% "http4s-circe"         % http4s_V,
  "org.typelevel"        %% "log4cats-core"        % "1.3.1",
  "org.typelevel"        %% "log4cats-slf4j"       % "1.3.1",
  "ch.qos.logback"        % "logback-classic"      % "1.2.6",
  "org.tpolecat"         %% "doobie-core"          % doobie_V,
  "org.tpolecat"         %% "doobie-h2"            % doobie_V,
  "org.tpolecat"         %% "doobie-hikari"        % doobie_V,
  "org.tpolecat"         %% "doobie-postgres"      % doobie_V,
  "org.tpolecat"         %% "doobie-scalatest"     % doobie_V  % Test,
  "org.mockito"          %% "mockito-scala"        % "1.16.46" % Test,
  "com.beachape"         %% "enumeratum"           % "1.7.0",
  "com.beachape"         %% "enumeratum-circe"     % "1.7.0",
  "io.circe"             %% "circe-config"         % "0.8.0",
  "io.circe"             %% "circe-core"           % "0.14.1",
  "io.circe"             %% "circe-generic"        % "0.14.1",
  "io.circe"             %% "circe-generic-extras" % "0.14.1",
  "io.circe"             %% "circe-optics"         % "0.14.1",
  "io.circe"             %% "circe-parser"         % "0.14.1",
  "io.circe"             %% "circe-refined"        % "0.14.1",
  "org.flywaydb"          % "flyway-core"          % "6.2.4",
  "com.github.jwt-scala" %% "jwt-core"             % "9.0.2",
  "org.postgresql"        % "postgresql"           % "42.3.1",
  "org.scalamock"        %% "scalamock"            % "5.1.0"   % Test,
  "org.scalatest"        %% "scalatest"            % "3.2.9"   % Test,
  "com.emarsys"          %% "scheduler"            % "0.4.5",
  "javax.mail"            % "mail"                 % "1.4.7",
  "com.minosiants"       %% "pencil"               % "0.6.7",
  "io.github.jmcardon"   %% "tsec-password"        % "0.3.0-M2",
  "org.scorexfoundation" %% "scrypto"              % "2.1.10"
//  "co.fs2"               %% "fs2-core"             % "2.5.1",
//  "co.fs2"               %% "fs2-io"               % "2.5.1"
//  "io.monix"                 %% "monix"                    % "3.4.0",
//  "org.typelevel"            %% "simulacrum"               % "1.0.1",
//  "org.scalatestplus"        %% "scalatestplus-scalacheck" % scalaTestVersion % Test,
//  "org.scalatestplus"        %% "selenium-2-45"            % scalaTestVersion % Test,
//  "org.tpolecat"             %% "atto-core"                % "0.8.0",
//  "org.fusesource.leveldbjni" % "leveldbjni-all"           % "1.8",
//  "org.scalaj"               %% "scalaj-http"              % "2.4.2"          % Test,
//  "org.typelevel"            %% "cats-tagless-macros"      % catsTaglessVersion,
//  "org.slf4j"                 % "slf4j-nop"                % "1.6.4",
//  "io.scalaland"             %% "chimney"                  % dtoMapperChimneyVersion,
//  "com.github.pureconfig"    %% "pureconfig"               % "0.14.0",

//  "javax.mail"                % "mail"                          % "1.4.7", //works
//  "dev.profunktor"           %% "http4s-jwt-auth"               % "1.0.0",
)

addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full
)

run / fork := true
