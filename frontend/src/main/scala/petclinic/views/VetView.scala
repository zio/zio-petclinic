package petclinic.views

import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.Component
import petclinic.models._

case class VetView(vets: List[Vet]) extends Component {
  def body = div(
    // todo: get all vets
  )
}
