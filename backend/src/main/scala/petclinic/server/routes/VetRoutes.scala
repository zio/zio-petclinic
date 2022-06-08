package petclinic.server.routes

import petclinic.services.VetService
import zhttp.http._
import zio.json.EncoderOps

object VetRoutes {

  val routes: Http[VetService, Throwable, Request, Response] = Http.collectZIO[Request] {
    case Method.GET -> !! / "veterinarians" =>
      VetService.getAll.map(vets => Response.json(vets.toJson))

    case Method.GET -> !! / "veterinarians" / id =>
      for {
        vetId <- ServerUtils.parseVetId(id)
        vet   <- VetService.get(vetId)
      } yield Response.json(vet.toJson)

  }

}
