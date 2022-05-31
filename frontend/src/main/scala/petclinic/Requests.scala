package petclinic

import petclinic.models._
import com.raquo.laminar.api.L.{Owner => _, _}
import sttp.capabilities
import sttp.client3._
import zio.json._
import zio.json.internal.RetractReader

import scala.concurrent.Future

object Requests {
  private val backend: SttpBackend[Future, capabilities.WebSockets] = FetchBackend()

  def getRequest[B: JsonCodec](path: Any*): EventStream[B] = {
    val request = quickRequest.get(uri"http://localhost:8080/$path")
    EventStream.fromFuture(backend.send(request)).map { response =>
      response.body.fromJson[B] match {
        case Right(b) => b
        case Left(e)  => throw new Error(s"Error parsing JSON: $e")
      }
    }
  }

  def postRequest[In: JsonEncoder, Out: JsonDecoder](body: In)(path: Any*): EventStream[Out] = {
    val request = quickRequest.post(uri"http://localhost:8080/$path").body(body.toJson)
    EventStream.fromFuture(backend.send(request)).map { response =>
      response.body.fromJson[Out] match {
        case Right(b) => b
        case Left(e)  => throw new Error(s"Error parsing JSON: $e")
      }
    }
  }

  def patchRequest[In: JsonEncoder, Out: JsonDecoder](body: In)(path: Any*): EventStream[Out] = {
    val request = quickRequest.patch(uri"http://localhost:8080/$path").body(body.toJson)
    EventStream.fromFuture(backend.send(request)).map { response =>
      response.body.fromJson[Out] match {
        case Right(b) => b
        case Left(e)  => throw new Error(s"Error parsing JSON: $e")
      }
    }
  }

  def getPets(ownerId: OwnerId): EventStream[List[Pet]] =
    getRequest[List[Pet]]("owners", ownerId.id, "pets")

  def addPet(createPet: CreatePet): EventStream[Pet] =
    postRequest[CreatePet, Pet](createPet)("pets")

  def updatePet(petId: PetId, updatePet: UpdatePet): EventStream[Unit] =
    patchRequest[UpdatePet, Unit](updatePet)("pets", petId.id)

  def getOwner(id: OwnerId): EventStream[Owner] =
    getRequest[Owner]("owners", id.id)

  def getVisits(petId: PetId): EventStream[List[Visit]] =
    getRequest[List[Visit]]("pets", petId.id, "visits")

  def addOwner(owner: Owner): EventStream[Owner] =
    postRequest[Owner, Owner](owner)("owners")

  def addVisit(petId: PetId, createVisit: CreateVisit): EventStream[Visit] =
    postRequest[CreateVisit, Visit](createVisit)("pets", petId.id, "visits")

  def updateVisit(visitId: VisitId, updateVisit: UpdateVisit): EventStream[Unit] =
    patchRequest[UpdateVisit, Unit](updateVisit)("visits", visitId.id)

  implicit lazy val unitDecoder: JsonDecoder[Unit] =
    new JsonDecoder[Unit] {
      override def unsafeDecode(trace: List[JsonError], in: RetractReader): Unit = ()
    }
}
