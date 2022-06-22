package petclinic.server.routes

import petclinic.models._
import petclinic.server.routes.ServerUtils.{parseBody, parseOwnerId}
import petclinic.services.{OwnerService, PetService}
import zhttp.http._
import zio.json._

object OwnerRoutes {

  val routes: Http[OwnerService, Throwable, Request, Response] =
    Http.collectZIO[Request] {

      case Method.GET -> !! / "owners" =>
        for {
          owners <- OwnerService.getAll
        } yield Response.json(owners.toJson)

      case Method.GET -> !! / "owners" / id =>
        for {
          id    <- parseOwnerId(id)
          owner <- OwnerService.get(id)
        } yield Response.json(owner.toJson)

      case req @ Method.POST -> !! / "owners" =>
        for {
          createOwner <- parseBody[CreateOwner](req)
          owner <-
            OwnerService.create(
              createOwner.firstName,
              createOwner.lastName,
              createOwner.address,
              createOwner.phone,
              createOwner.email
            )
        } yield Response.json(owner.toJson)

      case req @ Method.PATCH -> !! / "owners" / id =>
        for {
          ownerId     <- parseOwnerId(id)
          updateOwner <- parseBody[UpdateOwner](req)
          _ <- OwnerService.update(
                 ownerId,
                 updateOwner.firstName,
                 updateOwner.lastName,
                 updateOwner.address,
                 updateOwner.phone,
                 updateOwner.email
               )
        } yield Response.ok

      case Method.DELETE -> !! / "owners" / id =>
        for {
          id <- parseOwnerId(id)
          _  <- OwnerService.delete(id)
        } yield Response.ok
    }
}
