package petclinic.server.routes

import petclinic.services.{OwnerService, PetService}
import zhttp.http._
import zio.ZIO
import zio.json._
import petclinic.models._

object OwnerRoutes {

  val routes: Http[OwnerService with PetService, Throwable, Request, Response] =
    Http.collectZIO[Request] {

      case Method.GET -> !! / "owners" =>
        OwnerService.getAll.map { owners =>
          Response.json(owners.toJson)
        }

      case Method.GET -> !! / "owners" / id =>
        for {
          id    <- OwnerId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid owner id"))
          owner <- OwnerService.get(id)
        } yield Response.json(owner.toJson)

      case Method.GET -> !! / "owners" / id / "pets" =>
        for {
          id <- OwnerId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid owner id"))
          pets <-
            PetService.getForOwner(id).catchAllCause(cause => ZIO.debug(cause.prettyPrint) *> ZIO.failCause(cause))
        } yield Response.json(pets.toJson)

      case req @ Method.POST -> !! / "owners" =>
        for {
          body        <- req.bodyAsString.orElseFail(AppError.MissingBodyError)
          createOwner <- ZIO.from(body.fromJson[CreateOwner]).mapError(AppError.JsonDecodingError)
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
          body        <- req.bodyAsString.orElseFail(AppError.MissingBodyError)
          ownerId     <- OwnerId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid owner id"))
          updateOwner <- ZIO.from(body.fromJson[UpdateOwner]).mapError(AppError.JsonDecodingError)
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
          id <- OwnerId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid owner id"))
          _  <- OwnerService.delete(id)
        } yield Response.ok
    }
}
