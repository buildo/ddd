import sbt._

object Dependencies {
  val commonResolvers = List(
    Resolver.bintrayRepo("buildo", "maven"),
  )

  val V = new {
    val wiro = "0.6.13"
    val circe = "0.10.1"
    val zio = "1.0.3"
  }

  val wiro = "io.buildo" %% "wiro-http-server" % V.wiro
  val circeCore = "io.circe" %% "circe-core" % V.circe
  val circeGeneric = "io.circe" %% "circe-generic" % V.circe
  val circeJavaTime = "io.buildo" %% "java-time-circe-codecs" % "0.2.0"
  val pureconfig = "com.github.pureconfig" %% "pureconfig" % "0.9.2"
  val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.1.5"
  val akkaHttpCirce = "de.heikoseeberger" %% "akka-http-circe" % "1.22.0"
  val munit = "org.scalameta" %% "munit" % "0.7.5" % Test
  val catsCore = "org.typelevel" %% "cats-core" % "1.4.0"
  val zio = "dev.zio" %% "zio" % V.zio

  val dependencies =
    List(
      wiro,
      circeCore,
      circeGeneric,
      circeJavaTime,
      pureconfig,
      akkaHttp,
      akkaHttpCirce,
      munit,
      zio,
    )
}
