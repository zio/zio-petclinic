package petclinic.views

import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.{Component, Requests}
import petclinic.models._

case class PetView(pet: Pet, appointments: Option[List[Appointment]]) extends Component {
  def body = div(
    div(pet.name),
    div(pet.birthdate.toString),
    // todo: optionally display appointment(s)
    div(
      div("Appointments"),
      pre(
        children <-- Requests.getAppointments(pet.id).map(_.map(PetView(_)))
      )
    )
  )
}
