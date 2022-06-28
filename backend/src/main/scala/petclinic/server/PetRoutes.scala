package petclinic.server

import zio._
import zio.json._
import zhttp.http._
import petclinic.services.PetService
import ServerUtils._
import petclinic.models.api.{CreatePet, UpdatePet}

final case class PetRoutes(service: PetService) {

  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {

    case Method.GET -> !! / "pets" / id =>
      for {
        id  <- parsePetId(id)
        pet <- service.get(id)
      } yield Response.json(pet.toJson)

    case Method.GET -> !! / "pets" =>
      service.getAll.map(pets => Response.json(pets.toJson))

    case Method.GET -> !! / "owners" / id / "pets" =>
      for {
        id   <- parseOwnerId(id)
        pets <- service.getForOwner(id)
      } yield Response.json(pets.toJson)

    case req @ Method.POST -> !! / "pets" =>
      for {
        createPet <- parseBody[CreatePet](req)
        pet       <- service.create(createPet.name, createPet.birthdate, createPet.species, createPet.ownerId)
      } yield Response.json(pet.toJson)

    case req @ Method.PATCH -> !! / "pets" / id =>
      for {
        petId     <- parsePetId(id)
        updatePet <- parseBody[UpdatePet](req)
        _         <- service.update(petId, updatePet.name, updatePet.birthdate, updatePet.species, updatePet.ownerId)
      } yield Response.ok

    case Method.DELETE -> !! / "pets" / id =>
      for {
        id <- parsePetId(id)
        _  <- service.delete(id)
      } yield Response.ok

  }

}

object PetRoutes {

  val layer: URLayer[PetService, PetRoutes] = ZLayer.fromFunction(PetRoutes.apply _)

}
