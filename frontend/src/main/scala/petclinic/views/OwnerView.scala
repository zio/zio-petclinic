package petclinic.views

import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.{Component, Requests}
import petclinic.models._

case class OwnerView(owner: Owner, pets: Option[List[Pet]]) extends Component {
  def body = div(
    div(s"${owner.firstName} ${owner.lastName}"),
    div(owner.address),
    div(owner.phone),
    div(owner.email),
    // todo: optionally display pet(s)
    div(
      div("Pets"),
      pre(
        children <-- Requests.getPets(owner.id).map(_.map(PetView(_)))
      )
    )
  )
}
