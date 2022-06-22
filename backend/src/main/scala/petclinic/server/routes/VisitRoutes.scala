package petclinic.server.routes

import zio._
import zio.json._
import zhttp.http._
import petclinic.services.VisitService
import petclinic.server.routes.ServerUtils._
import petclinic.models.api.{CreateVisit, UpdateVisit}

final case class VisitRoutes(service: VisitService) {

  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {

    case Method.GET -> !! / "pets" / id / "visits" =>
      for {
        petId  <- parsePetId(id)
        visits <- service.getForPet(petId)
      } yield Response.json(visits.toJson)

    case req @ Method.POST -> !! / "pets" / id / "visits" =>
      for {
        petId       <- parsePetId(id)
        createVisit <- parseBody[CreateVisit](req)
        visits      <- service.create(petId, createVisit.date, createVisit.description)
      } yield Response.json(visits.toJson)

    case req @ Method.PATCH -> !! / "visits" / id =>
      for {
        visitId     <- parseVisitId(id)
        updateVisit <- parseBody[UpdateVisit](req)
        _           <- service.update(visitId, updateVisit.date, updateVisit.description)
      } yield Response.ok

    case Method.DELETE -> !! / "visits" / id =>
      for {
        visitId <- parseVisitId(id)
        _       <- service.delete(visitId)
      } yield Response.ok

  }

}

object VisitRoutes {

  val layer: URLayer[VisitService, VisitRoutes] = ZLayer.fromFunction(VisitRoutes.apply _)

}
