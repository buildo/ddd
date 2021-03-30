package apiseed
package error

import wiro.server.akkaHttp._

import akka.http.scaladsl.model.{ContentType, HttpEntity, HttpResponse, MediaTypes, StatusCodes}
import io.circe.syntax._

trait WiroErrorResponses {
  implicit def errorToResponse: ToHttpResponse[GenericError] =
    error =>
      HttpResponse(
        status = StatusCodes.InternalServerError,
        entity = HttpEntity(ContentType(MediaTypes.`application/json`), error.asJson.noSpaces),
      )
}
