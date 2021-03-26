package apiseed
package controller

import error.GenericError

import scala.concurrent.Future
import wiro.annotation._
import zio.{IO, Runtime, ZEnv}

@path("apiseed")
trait HelloWorldController {

  @query
  def helloworld(): Future[Either[GenericError, String]]
}

object HelloWorldController {

  def create(implicit runtime: Runtime[ZEnv]): HelloWorldController =
    new HelloWorldController with ZIOController {

      override def helloworld(): Future[Either[GenericError, String]] =
        IO.succeed("Hello world!").toWiroFuture
    }
}
