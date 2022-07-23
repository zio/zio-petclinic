package petclinic.server

import petclinic.models.api.{CreateOwner, UpdateOwner}
import petclinic.server.ServerUtils.{parseBody, parseOwnerId}
import petclinic.services.OwnerService
import zhttp.http._
import zio._
import zio.json._

/** OwnerRoutes is a service that provides the routes for the OwnerService API.
  * The routes serve the "owners" endpoint.
  */
final case class OwnerRoutes(service: OwnerService) {

  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {

    // Gets all of the Owners in the database and returns them as JSON.
    case Method.GET -> !! / "owners" =>
      service.getAll.map(owners => Response.json(owners.toJson))

    // Gets a single Owner found by their parsed ID and returns it as JSON.
    case Method.GET -> !! / "owners" / id =>
      for {
        id    <- parseOwnerId(id)
        owner <- service.get(id)
      } yield Response.json(owner.toJson)

    // Creates a new Owner from the parsed CreateOwner request body and returns it as JSON.
    case req @ Method.POST -> !! / "owners" =>
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

    /** Updates a single Owner found by their parsed ID using the information
      * parsed from the UpdateOwner request and returns a 200 status code
      * indicating success.
      */
    case req @ Method.PATCH -> !! / "owners" / id =>
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

    // Deletes a single Owner found by their parsed ID and returns a 200 status code indicating success.
    case Method.DELETE -> !! / "owners" / id =>
      for {
        id <- parseOwnerId(id)
        _  <- service.delete(id)
      } yield Response.ok

  }

}

/** Here in the companion object we define the layer that will be used to
  * provide the routes for the OwnerService API to the run method in our Main
  * file.
  */
object OwnerRoutes {

  val layer: URLayer[OwnerService, OwnerRoutes] = ZLayer.fromFunction(OwnerRoutes.apply _)

}
