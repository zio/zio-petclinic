package petclinic.server.routes

import petclinic.models._
import petclinic.server.routes.ServerUtils._
import petclinic.services.{PetService, VisitService}
import zhttp.http._
import zio.json._

object PetRoutes {

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
