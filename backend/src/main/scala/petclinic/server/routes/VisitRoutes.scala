package petclinic.server.routes

import petclinic.models._
import petclinic.server.routes.ServerUtils._
import petclinic.services.VisitService
import zhttp.http._
import zio.json._

object VisitRoutes {

  val routes: Http[VisitService, Throwable, Request, Response] =
    Http.collectZIO[Request] {

      case Method.GET -> !! / "pets" / id / "visits" =>
        for {
          petId  <- parsePetId(id)
          visits <- VisitService.getForPet(petId)
        } yield Response.json(visits.toJson)

      case req @ Method.POST -> !! / "pets" / id / "visits" =>
        for {
          petId       <- parsePetId(id)
          createVisit <- parseBody[CreateVisit](req)
          visits      <- VisitService.create(petId, createVisit.date, createVisit.description)
        } yield Response.json(visits.toJson)

      case req @ Method.PATCH -> !! / "visits" / id =>
        for {
          visitId     <- parseVisitId(id)
          updateVisit <- parseBody[UpdateVisit](req)
          _           <- VisitService.update(visitId, updateVisit.date, updateVisit.description)
        } yield Response.ok

      case Method.DELETE -> !! / "visits" / id =>
        for {
          visitId <- parseVisitId(id)
          _       <- VisitService.delete(visitId)
        } yield Response.ok
    }

}
