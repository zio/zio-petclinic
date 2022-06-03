package petclinic.server.routes

import petclinic.services.{PetService, VisitService}
import zhttp.http._
import zio.{IO, ZIO}
import zio.json._
import petclinic.models._

// TODO:
//  1. Move into its own file
//  2. Figure out how to create a general "Id Parsing" method
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
}

object PetRoutes {
  import ServerUtils._

  val routes: Http[PetService with VisitService, Throwable, Request, Response] = Http.collectZIO[Request] {

    case Method.GET -> !! / "pets" / id =>
      for {
        id  <- parsePetId(id)
        pet <- PetService.get(id)
      } yield Response.json(pet.toJson)

    case Method.GET -> !! / "pets" =>
      PetService.getAll.map(pets => Response.json(pets.toJson))

    case req @ Method.POST -> !! / "pets" =>
      for {
        createPet <- parseBody[CreatePet](req)
        pet       <- PetService.create(createPet.name, createPet.birthdate, createPet.species, createPet.ownerId)
      } yield Response.json(pet.toJson)

    case req @ Method.PATCH -> !! / "pets" / id =>
      for {
        petId     <- parsePetId(id)
        updatePet <- parseBody[UpdatePet](req)
        _         <- PetService.update(petId, updatePet.name, updatePet.birthdate, updatePet.species, updatePet.ownerId)
      } yield Response.ok

    case Method.DELETE -> !! / "pets" / id =>
      for {
        id <- parsePetId(id)
        _  <- PetService.delete(id)
      } yield Response.ok

  }

}
