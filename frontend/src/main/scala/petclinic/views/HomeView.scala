package petclinic.views

import com.raquo.laminar.api.L._
import petclinic.{Component, Style}

final case class HomeView() extends Component {
  val body: Div =
    div(
      cls("text-l text-justify m-5"),
      div(
        cls("mb-4"),
        "Welcome to ZIO Pet Clinic: a fullstack, idiomatic web app that serves as an example for best utilizing ZIO and the libraries within its ecosystem."
      ),
      div(
        "Navigate to the Owners tab to preview a list of preloaded pet owners. Select an owner to do things like view their personal information, add pets, update visits for a pet, and create owners."
      )
    )
}
