package petclinic.views

import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.Component
import petclinic.models._

case class AppointmentView(appointment: Appointment) extends Component {
  def body = div(
    div(appointment.date.toString)
    // todo: display vet associated with appointment
  )
}
