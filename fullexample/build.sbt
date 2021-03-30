import Dependencies._

inThisBuild(
  List(
    name := "apiseed",
    version := "1.0.0",
    scalaVersion := "2.12.12",
    resolvers ++= commonResolvers,
    testFrameworks += new TestFramework("munit.Framework"),
  ) ++ scalafixSettings,
)

lazy val root = project
  .in(file("."))
  .settings(
    libraryDependencies ++= dependencies,
  )

lazy val core = project
  .settings(
    libraryDependencies ++= Seq(
      catsCore,
    ),
  )
