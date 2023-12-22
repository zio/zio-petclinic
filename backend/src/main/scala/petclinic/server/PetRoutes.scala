package petclinic.server

import petclinic.models.api.{CreatePet, UpdatePet}
import petclinic.server.ServerUtils._
import petclinic.services.PetService
import zio.http._
import zio._
import zio.json._

/** PetRoutes is a service that provides the routes for the PetService API. The
  * routes serve the "pets" endpoint.
  */
final case class PetRoutes(service: PetService) {

  val routes: Routes[Any, Throwable] =
    Routes(
      // Gets all of the Pets in the database and returns them as JSON.
      Method.GET / "pets" -> handler(service.getAll.map(pets => Response.json(pets.toJson))),

      // Gets a single Pet found by their parsed ID and returns it as JSON.
      Method.GET / "pets" / string("id") -> handler { (id: String, req: Request) =>
        for {
          id  <- parsePetId(id)
          pet <- service.get(id)
        } yield Response.json(pet.toJson)
      },

      // Gets all of the Pets in the database associated with a particular owner and returns them as JSON.
      Method.GET / "owners" / string("id") / "pets" -> handler { (id: String, req: Request) =>
        for {
          id   <- parseOwnerId(id)
          pets <- service.getForOwner(id)
        } yield Response.json(pets.toJson)
      },

      // Creates a new Pet from the parsed CreatePet request body and returns it as JSON.
      Method.POST / "pets" -> handler { req: Request =>
        for {
          createPet <- parseBody[CreatePet](req)
          pet       <- service.create(createPet.name, createPet.birthdate, createPet.species, createPet.ownerId)
        } yield Response.json(pet.toJson)
      },

      /** Updates a single Pet found by their parsed ID using the information
        * parsed from the UpdatePet request body and returns a 200 status code
        * indicating success.
        */
      Method.PATCH / "pets" / string("id") -> handler { (id: String, req: Request) =>
        for {
          petId     <- parsePetId(id)
          updatePet <- parseBody[UpdatePet](req)
          _         <- service.update(petId, updatePet.name, updatePet.birthdate, updatePet.species, updatePet.ownerId)
        } yield Response.ok
      },

      // Deletes a single Pet found by their parsed ID and returns a 200 status code indicating success.
      Method.DELETE / "pets" / string("id") -> handler { (id: String, req: Request) =>
        for {
          id <- parsePetId(id)
          _  <- service.delete(id)
        } yield Response.ok
      }
    )

}

/** Here in the companion object we define the layer that will be used to
  * provide the routes for the PetService API to the run method in our Main
  * file.
  */
object PetRoutes {

  val layer: URLayer[PetService, PetRoutes] = ZLayer.fromFunction(PetRoutes.apply _)

}
