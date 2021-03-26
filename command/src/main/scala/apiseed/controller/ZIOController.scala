package apiseed
package controller

import scala.concurrent.Future
import zio.{IO, Runtime, ZEnv}

import apiseed.error.GenericError

trait ZIOController {
  implicit class ZIOController[A](io: IO[GenericError, A]) {
    def toWiroFuture(implicit runtime: Runtime[ZEnv]): Future[Either[GenericError, A]] = {
      runtime.unsafeRunToFuture(io.either)
    }
  }
}
