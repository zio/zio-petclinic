package petclinic.server

import petclinic.models.{OwnerId, PetId, VetId, VisitId}
import zio.json._
import zio.http.Request
import zio.{IO, ZIO}

/** ServerUtils houses helper functions for parsing various elements of the API
  * including request bodies and specific IDs.
  *
  * This allows us to explicitly define what we are parsing and to provide
  * custom error messages.
  */
object ServerUtils {

  /** Parses a request using a JSON decoder type class for the A type to decode
    * an A from the request's string body. In the case of a failure we are
    * returning a custom AppError type
    */
  def parseBody[A: JsonDecoder](request: Request): IO[AppError, A] =
    for {
      body   <- request.body.asString.orElseFail(AppError.MissingBodyError)
      parsed <- ZIO.from(body.fromJson[A]).mapError(AppError.JsonDecodingError)
    } yield parsed

  /** Parses a PetId from the provided string.
    */
  def parsePetId(id: String): IO[AppError.InvalidIdError, PetId] =
    PetId.fromString(id).orElseFail(AppError.InvalidIdError("Invalid pet id"))

  /** Parses a VisitId from the provided string.
    */
  def parseVisitId(id: String): IO[AppError.InvalidIdError, VisitId] =
    VisitId.fromString(id).orElseFail(AppError.InvalidIdError("Invalid visit id"))

  /** Parses a VetId from the provided string.
    */
  def parseVetId(id: String): IO[AppError.InvalidIdError, VetId] =
    VetId.fromString(id).orElseFail(AppError.InvalidIdError("Invalid vet id"))

  /** Parses an OwnerId from the provided string.
    */
  def parseOwnerId(id: String): IO[AppError.InvalidIdError, OwnerId] =
    OwnerId.fromString(id).orElseFail(AppError.InvalidIdError("Invalid owner id"))
}
