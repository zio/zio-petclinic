package petclinic.server

import petclinic.models.api.{CreatePet, UpdatePet}
import petclinic.server.ServerUtils._
import petclinic.services.PetService
import zhttp.http._
import zio._
import zio.json._

/** PetRoutes is a service that provides the routes for the PetService API. The
  * routes serve the "pets" endpoint.
  */
final case class PetRoutes(service: PetService) {

  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {

    // Gets all of the pets in the database and returns them as JSON.
    case Method.GET -> !! / "pets" =>
      service.getAll.map(pets => Response.json(pets.toJson))

    // Gets a single pet found by their parsed ID and returns it as JSON.
    case Method.GET -> !! / "pets" / id =>
      for {
        id  <- parsePetId(id)
        pet <- service.get(id)
      } yield Response.json(pet.toJson)

    // Gets all of the pets in the database associated with a particular owner and returns them as JSON.
    case Method.GET -> !! / "owners" / id / "pets" =>
      for {
        id   <- parseOwnerId(id)
        pets <- service.getForOwner(id)
      } yield Response.json(pets.toJson)

    // Creates a new pet from the parsed CreatePet request body and returns it as JSON.
    case req @ Method.POST -> !! / "pets" =>
      for {
        createPet <- parseBody[CreatePet](req)
        pet       <- service.create(createPet.name, createPet.birthdate, createPet.species, createPet.ownerId)
      } yield Response.json(pet.toJson)

    /** Updates a single pet found by their parsed ID using the information
      * parsed from the UpdatePet request and returns a 200 status code
      * indicating success.
      */
    case req @ Method.PATCH -> !! / "pets" / id =>
      for {
        petId     <- parsePetId(id)
        updatePet <- parseBody[UpdatePet](req)
        _         <- service.update(petId, updatePet.name, updatePet.birthdate, updatePet.species, updatePet.ownerId)
      } yield Response.ok

    // Deletes a single pet found by their parsed ID and returns a 200 status code indicating success.
    case Method.DELETE -> !! / "pets" / id =>
      for {
        id <- parsePetId(id)
        _  <- service.delete(id)
      } yield Response.ok

  }

}

/** Here in the companion object we define the layer that will be used to
  * provide the routes for the PetService API.
  */
object PetRoutes {

  val layer: URLayer[PetService, PetRoutes] = ZLayer.fromFunction(PetRoutes.apply _)

}
