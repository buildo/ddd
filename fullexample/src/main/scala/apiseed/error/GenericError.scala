package apiseed
package error

import io.circe.generic.JsonCodec

@JsonCodec case class GenericError(
  message: String = "",
)
