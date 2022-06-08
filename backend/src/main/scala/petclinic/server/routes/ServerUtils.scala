package petclinic.server.routes

import petclinic.models.{PetId, VetId, VisitId}
import zhttp.http.Request
import zio.json._
import zio.{IO, ZIO}

object ServerUtils {
  def parseBody[A: JsonDecoder](request: Request): IO[AppError, A] =
    for {
      body   <- request.bodyAsString.orElseFail(AppError.MissingBodyError)
      parsed <- ZIO.from(body.fromJson[A]).mapError(AppError.JsonDecodingError)
    } yield parsed

  def parsePetId(id: String): IO[AppError.JsonDecodingError, PetId] =
    PetId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid pet id"))

  def parseVisitId(id: String): IO[AppError.JsonDecodingError, VisitId] =
    VisitId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid pet id"))

  def parseVetId(id: String): IO[AppError.JsonDecodingError, VetId] =
    VetId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid vet id"))
}
