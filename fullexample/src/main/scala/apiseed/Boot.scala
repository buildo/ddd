package apiseed

import config.Config
import controller._
import error.WiroErrorResponses

import wiro.server.akkaHttp._
import wiro.server.akkaHttp.FailSupport._

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

import zio.Runtime

object Boot extends RouterDerivationModule with WiroErrorResponses {

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("apiseed")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val _: ExecutionContext = system.dispatcher
    implicit val runtime = Runtime.default
    val config = pureconfig.loadConfigOrThrow[Config]

    val helloworldController = HelloWorldController.create
    val helloworldRouter = deriveRouter[HelloWorldController](helloworldController)

    new HttpRPCServer(
      config = config.wiro,
      routers = List(helloworldRouter),
    )
    ()
  }
}
