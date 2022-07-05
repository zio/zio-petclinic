package petclinic.server

import petclinic.services.VetService
import zhttp.http._
import zio._
import zio.json.EncoderOps

/** VetRoutes is a service that provides the routes for the VetService API. The
  * routes serve the "veterinarians" endpoint.
  */
final case class VetRoutes(service: VetService) {

  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {

    // Gets all of the vets in the database and returns them as JSON.
    case Method.GET -> !! / "veterinarians" =>
      service.getAll.map(vets => Response.json(vets.toJson))

    // Gets a single vet found by their parsed ID and returns it as JSON.
    case Method.GET -> !! / "veterinarians" / id =>
      for {
        vetId <- ServerUtils.parseVetId(id)
        vet   <- service.get(vetId)
      } yield Response.json(vet.toJson)

  }

}

/** Here in the companion object we define the layer that will be used to
  * provide the routes for the VetService API.
  */
object VetRoutes {

  val layer: URLayer[VetService, VetRoutes] = ZLayer.fromFunction(VetRoutes.apply _)

}
