package petclinic.server

import petclinic.models.api.{CreateOwner, UpdateOwner}
import petclinic.server.ServerUtils.{parseBody, parseOwnerId}
import petclinic.services.OwnerService
import zio._
import zio.http._
import zio.json._

/** OwnerRoutes is a service that provides the routes for the OwnerService API.
  * The routes serve the "owners" endpoint.
  */
final case class OwnerRoutes(service: OwnerService) {

  val routes: Routes[Any, Throwable] =
    Routes(
      // Gets all of the Owners in the database and returns them as JSON.
      Method.GET / "owners" -> handler(service.getAll.map(owners => Response.json(owners.toJson))),

      // Gets a single Owner found by their parsed ID and returns it as JSON.
      Method.GET / "owners" / string("id") -> handler { (id: String, req: Request) =>
        for {
          id    <- parseOwnerId(id)
          owner <- service.get(id)
        } yield Response.json(owner.toJson)
      },

      // Creates a new Owner from the parsed CreateOwner request body and returns it as JSON.
      Method.POST / "owners" -> handler { req: Request =>
        for {
          createOwner <- parseBody[CreateOwner](req)
          owner <-
            service.create(
              createOwner.firstName,
              createOwner.lastName,
              createOwner.address,
              createOwner.phone,
              createOwner.email
            )
        } yield Response.json(owner.toJson)
      },

      /** Updates a single Owner found by their parsed ID using the information
        * parsed from the UpdateOwner request and returns a 200 status code
        * indicating success.
        */
      Method.PATCH / "owners" / string("id") -> handler { (id: String, req: Request) =>
        for {
          ownerId     <- parseOwnerId(id)
          updateOwner <- parseBody[UpdateOwner](req)
          _ <- service.update(
                 ownerId,
                 updateOwner.firstName,
                 updateOwner.lastName,
                 updateOwner.address,
                 updateOwner.phone,
                 updateOwner.email
               )
        } yield Response.ok
      },

      // Deletes a single Owner found by their parsed ID and returns a 200 status code indicating success.
      Method.DELETE / "owners" / string("id") -> handler { (id: String, req: Request) =>
        for {
          id <- parseOwnerId(id)
          _  <- service.delete(id)
        } yield Response.ok
      }
    )
}

/** Here in the companion object we define the layer that will be used to
  * provide the routes for the OwnerService API to the run method in our Main
  * file.
  */
object OwnerRoutes {

  val layer: URLayer[OwnerService, OwnerRoutes] = ZLayer.fromFunction(OwnerRoutes.apply _)

}
