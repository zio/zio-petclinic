package petclinic.server

import petclinic.services.VetService
import zio._
import zio.http._
import zio.json.EncoderOps

/** VetRoutes is a service that provides the routes for the VetService API. The
  * routes serve the "veterinarians" endpoint.
  */
final case class VetRoutes(service: VetService) {

  val routes: Routes[Any, Throwable] =
    Routes(
      // Gets all of the Vets in the database and returns them as JSON.
      Method.GET / "veterinarians" -> handler(service.getAll.map(vets => Response.json(vets.toJson))),

      // Gets a single Vet found by their parsed ID and returns it as JSON.
      Method.GET / "veterinarians" / string("id") -> handler { (id: String, req: Request) =>
        for {
          vetId <- ServerUtils.parseVetId(id)
          vet   <- service.get(vetId)
        } yield Response.json(vet.toJson)
      }
    )

}

/** Here in the companion object we define the layer that will be used to
  * provide the routes for the VetService API to the run method in our Main
  * file.
  */
object VetRoutes {

  val layer: URLayer[VetService, VetRoutes] = ZLayer.fromFunction(VetRoutes.apply _)

}
