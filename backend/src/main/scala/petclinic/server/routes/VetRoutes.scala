package petclinic.server.routes

import zio._
import zhttp.http._
import zio.json.EncoderOps
import petclinic.services.VetService

final case class VetRoutes(service: VetService) {

  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {

    case Method.GET -> !! / "veterinarians" =>
      service.getAll.map(vets => Response.json(vets.toJson))

    case Method.GET -> !! / "veterinarians" / id =>
      for {
        vetId <- ServerUtils.parseVetId(id)
        vet   <- service.get(vetId)
      } yield Response.json(vet.toJson)

  }

}

object VetRoutes {

  val layer: URLayer[VetService, VetRoutes] = ZLayer.fromFunction(VetRoutes.apply _)

}
