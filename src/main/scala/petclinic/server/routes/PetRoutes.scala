package petclinic.server.routes

import petclinic.models.{OwnerId, PetId, Species}
import petclinic.services.PetService
import zhttp.http._
import zio.ZIO
import zio.json._

object PetRoutes {

  final case class CreatePet(name: String, birthdate: java.time.LocalDate, species: Species, ownerId: OwnerId)

  object CreatePet {
    implicit val codec: JsonCodec[CreatePet] = DeriveJsonCodec.gen[CreatePet]
  }

  final case class UpdatePet(
      id: PetId,
      name: Option[String],
      birthdate: Option[java.time.LocalDate],
      species: Option[Species],
      ownerId: Option[OwnerId]
  )

  object UpdatePet {

    implicit val codec: JsonCodec[UpdatePet] = DeriveJsonCodec.gen[UpdatePet]

  }

  val routes: Http[PetService, Throwable, Request, Response] = Http.collectZIO[Request] {
    // get a pet
    case Method.GET -> !! / "pets" / id =>
      for {
        id  <- PetId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid pet id"))
        pet <- PetService.get(id)
      } yield Response.json(pet.toJson)

    // get all pets
    case Method.GET -> !! / "pets" =>
      PetService.getAll.map(pets => Response.json(pets.toJson))

    // create pet
    case req @ Method.POST -> !! / "pets" =>
      for {
        body      <- req.bodyAsString.orElseFail(AppError.MissingBodyError)
        createPet <- ZIO.from(body.fromJson[CreatePet]).mapError(AppError.JsonDecodingError)
        pet       <- PetService.create(createPet.name, createPet.birthdate, createPet.species, createPet.ownerId)
      } yield Response.json(pet.toJson)

    // update pet
    case req @ Method.POST -> !! / "pets" =>
      for {
        body      <- req.bodyAsString.orElseFail(AppError.MissingBodyError)
        updatePet <- ZIO.from(body.fromJson[UpdatePet]).mapError(AppError.JsonDecodingError)
        _         <- PetService.update(updatePet.id, updatePet.name, updatePet.birthdate, updatePet.species, updatePet.ownerId)
      } yield Response.ok

    // delete pet
    case req @ Method.DELETE -> !! / "pets" / id =>
      for {
        id <- PetId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid pet id"))
        _  <- PetService.delete(id)
      } yield Response.ok
  }

}
