package petclinic.server

import petclinic.models.api.{CreateVisit, UpdateVisit}
import petclinic.server.ServerUtils._
import petclinic.services.VisitService
import zio.http._
import zio._
import zio.json._

/** VisitRoutes is a service that provides the routes for the VisitService API.
  * The routes serve the "visits" endpoint.
  */
final case class VisitRoutes(service: VisitService) {

  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {

    // Gets all of the Visits in the database associated with a particular pet and returns them as JSON.
    case Method.GET -> !! / "pets" / id / "visits" =>
      for {
        petId  <- parsePetId(id)
        visits <- service.getForPet(petId)
      } yield Response.json(visits.toJson)

    /** Creates a new Visit for a given pet (selected by their parsed id). The
      * visit information is parsed from the CreateVisit request body and the
      * visit is returned as JSON.
      */
    case req @ Method.POST -> !! / "pets" / id / "visits" =>
      for {
        petId       <- parsePetId(id)
        createVisit <- parseBody[CreateVisit](req)
        visits      <- service.create(petId, createVisit.date, createVisit.description)
      } yield Response.json(visits.toJson)

    /** Updates a single Visit found by its parsed ID using the information
      * parsed from the UpdateVisit request body and returns a 200 status code
      * indicating success.
      */
    case req @ Method.PATCH -> !! / "visits" / id =>
      for {
        visitId     <- parseVisitId(id)
        updateVisit <- parseBody[UpdateVisit](req)
        _           <- service.update(visitId, updateVisit.date, updateVisit.description)
      } yield Response.ok

    // Deletes a single Visit found by its parsed ID and returns a 200 status code indicating success.
    case Method.DELETE -> !! / "visits" / id =>
      for {
        visitId <- parseVisitId(id)
        _       <- service.delete(visitId)
      } yield Response.ok

  }

}

/** Here in the companion object we define the layer that will be used to
  * provide the routes for the VisitService API to the run method in our Main
  * file.
  */
object VisitRoutes {

  val layer: URLayer[VisitService, VisitRoutes] = ZLayer.fromFunction(VisitRoutes.apply _)

}
