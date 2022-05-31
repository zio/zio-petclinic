package petclinic.server.routes

import petclinic.services.{OwnerService, PetService}
import zhttp.http._
import zio.ZIO
import zio.json._
import petclinic.models._

object OwnerRoutes {

  final case class CreateOwner(firstName: String, lastName: String, address: String, phone: String, email: String)

  object CreateOwner {

    implicit val codec: JsonCodec[CreateOwner] = DeriveJsonCodec.gen[CreateOwner]

  }

  final case class UpdateOwner(
      id: OwnerId,
      firstName: Option[String],
      lastName: Option[String],
      address: Option[String],
      phone: Option[String],
      email: Option[String]
  )

  object UpdateOwner {
    implicit val codec: JsonCodec[UpdateOwner] = DeriveJsonCodec.gen[UpdateOwner]
  }

  val routes: Http[OwnerService with PetService, Throwable, Request, Response] =
    Http.collectZIO[Request] {

      case Method.GET -> !! / "owners" / id =>
        for {
          id    <- OwnerId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid owner id"))
          owner <- OwnerService.get(id)
        } yield Response.json(owner.toJson)

      case Method.GET -> !! / "owners" / id / "pets" =>
        for {
          _  <- ZIO.debug(s"GET /owners/$id/pets")
          id <- OwnerId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid owner id"))
          _  <- ZIO.debug(s"OWNER ID /owners/$id/pets")
          pets <-
            PetService.getForOwner(id).catchAllCause(cause => ZIO.debug(cause.prettyPrint) *> ZIO.failCause(cause))
          _ <- ZIO.debug(s"PETS $pets")
        } yield Response.json(pets.toJson)

      case Method.GET -> !! / "owners" =>
        OwnerService.getAll.map { owners =>
          Response.json(owners.toJson)
        }

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

      case req @ Method.POST -> !! / "owners" =>
        for {
          body        <- req.bodyAsString.orElseFail(AppError.MissingBodyError)
          updateOwner <- ZIO.from(body.fromJson[UpdateOwner]).mapError(AppError.JsonDecodingError)
          _ <- OwnerService.update(
                 updateOwner.id,
                 updateOwner.firstName,
                 updateOwner.lastName,
                 updateOwner.address,
                 updateOwner.phone,
                 updateOwner.email
               )
        } yield Response.ok

      case req @ Method.DELETE -> !! / "owners" / id =>
        for {
          id    <- OwnerId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid owner id"))
          owner <- OwnerService.delete(id)
        } yield Response.ok
    }
}
