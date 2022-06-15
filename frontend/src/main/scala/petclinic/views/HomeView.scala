package petclinic.views

import com.raquo.laminar.api.L._
import petclinic.{Component, Style}

final case class HomeView() extends Component {
  val body: Div = div(
    Style.header,
    "An idiomatic pet clinic application written with ZIO."
  )
}
