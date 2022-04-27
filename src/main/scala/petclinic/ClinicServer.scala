package petclinic

import petclinic.models.{Address, Owner, OwnerId}
import zhttp.http._
import zhttp.service.Server
import zio.ZIOAppDefault
import petclinic.services._
import zio.json._
import zio._

object ClinicServer extends ZIOAppDefault {

  final case class CreateOwner(firstName: String, lastName: String, address: String, phone: String)

  object CreateOwner {

    implicit val codec: JsonCodec[CreateOwner] = DeriveJsonCodec.gen[CreateOwner]

  }

  final case class UpdateOwner(
      id: OwnerId,
      firstName: Option[String],
      lastName: Option[String],
      address: Option[String],
      phone: Option[String]
  )

  object UpdateOwner {

    implicit val codec: JsonCodec[UpdateOwner] = DeriveJsonCodec.gen[UpdateOwner]

  }

  sealed trait AppError extends Throwable

  object AppError {

    case object MissingBodyError extends AppError

    case class JsonDecodingError(message: String) extends AppError

  }

  val ownerRoutes: Http[OwnerService, Throwable, Request, Response] =
    Http.collectZIO[Request] {

      // get one owner
      case Method.GET -> !! / "owners" / id =>
        for {
          id    <- OwnerId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid owner id"))
          owner <- OwnerService.get(id)
        } yield Response.json(owner.toJson)

      // get all owners
      case Method.GET -> !! / "owners" =>
        OwnerService.getAll.map { owners =>
          Response.json(owners.toJson)
        }

      // create owner
      case req @ Method.POST -> !! / "owners" =>
        for {
          body        <- req.bodyAsString.orElseFail(AppError.MissingBodyError)
          createOwner <- ZIO.from(body.fromJson[CreateOwner]).mapError(AppError.JsonDecodingError)
          owner <-
            OwnerService.create(createOwner.firstName, createOwner.lastName, createOwner.address, createOwner.phone)
        } yield Response.json(owner.toJson)

      // update owner
      case req @ Method.POST -> !! / "owners" =>
        for {
          body        <- req.bodyAsString.orElseFail(AppError.MissingBodyError)
          updateOwner <- ZIO.from(body.fromJson[UpdateOwner]).mapError(AppError.JsonDecodingError)
          _ <- OwnerService.update(
                 updateOwner.id,
                 updateOwner.firstName,
                 updateOwner.lastName,
                 updateOwner.address,
                 updateOwner.phone
               )
        } yield Response.ok

      // delete owner
      case req @ Method.DELETE -> !! / "owners" / id =>
        for {
          id    <- OwnerId.fromString(id).orElseFail(AppError.JsonDecodingError("Invalid owner id"))
          owner <- OwnerService.delete(id)
        } yield Response.ok
    }

  override val run: ZIO[Any, Throwable, Nothing] =
    Server
      .start(8080, ownerRoutes)
      .provide(Random.live, QuillContext.dataSourceLayer, OwnerServiceLive.layer)
}
