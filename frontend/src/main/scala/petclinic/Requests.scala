package petclinic

import petclinic.models._
import com.raquo.laminar.api.L.{Owner => _, _}
import sttp.capabilities
import sttp.client3._
import zio.json._

import scala.concurrent.Future

object Requests {
  private val backend: SttpBackend[Future, capabilities.WebSockets] = FetchBackend()

  private def formatGetFuture(filter: String): Future[Response[String]] = {
    val request = quickRequest.get(uri"http://localhost:8080/$filter")
    backend.send(request)
  }

  private def formatPostFuture(filter: String, body: String): Future[Response[String]] = {
    val request = quickRequest.post(uri"http://localhost:8080/$filter").body(body)
    backend.send(request)
  }

  // get owner by id
  def getOwner(id: OwnerId): EventStream[Owner] = {
    val future = formatGetFuture(s"owners/$id")
    EventStream.fromFuture(future).map { response =>
      response.body.fromJson[Owner] match {
        case Right(owner) => owner
        case Left(error)  => throw new Error(s"Error parsing JSON: $error")
      }
    }
  }

  // get all pets for owner
  def getPets(ownerId: OwnerId): EventStream[List[Pet]] = {
    val future = formatGetFuture(s"owners/$ownerId/pets")
    EventStream.fromFuture(future).map { response =>
      response.body.fromJson[List[Pet]] match {
        case Right(pets) => pets
        case Left(error) => throw new Error(s"Error parsing JSON: $error")
      }
    }
  }

  // get all appointments for pet
  def getAppointments(petId: PetId): EventStream[List[Appointment]] = {
    val future = formatGetFuture(s"pets/$petId/appointments")
    EventStream.fromFuture(future).map { response =>
      response.body.fromJson[List[Appointment]] match {
        case Right(appointments) => appointments
        case Left(error)         => throw new Error(s"Error parsing JSON: $error")
      }
    }
  }

  // add owner (can be used for update also?)
  def addOwner(owner: Owner): EventStream[Owner] = {
    val future = formatPostFuture("owners", owner.toJson)
    EventStream.fromFuture(future).map { response =>
      response.body.fromJson[Owner] match {
        case Right(owner) => owner
        case Left(error)  => throw new Error(s"Error parsing JSON: $error")
      }
    }
  }

  // add pet (can be used for update also?)
  def addPet(ownerId: OwnerId, pet: Pet): EventStream[Pet] = {
    val future = formatPostFuture(s"owners/$ownerId/pets", pet.toJson)
    EventStream.fromFuture(future).map { response =>
      response.body.fromJson[Pet] match {
        case Right(pet)  => pet
        case Left(error) => throw new Error(s"Error parsing JSON: $error")
      }
    }
  }

  // add appointment (can be used for update also?)
  def addAppointment(petId: PetId, appointment: Appointment): EventStream[Appointment] = {
    val future = formatPostFuture(s"pets/$petId/appointments", appointment.toJson)
    EventStream.fromFuture(future).map { response =>
      response.body.fromJson[Appointment] match {
        case Right(appointment) => appointment
        case Left(error)        => throw new Error(s"Error parsing JSON: $error")
      }
    }
  }

}
