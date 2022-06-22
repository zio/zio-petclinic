package petclinic.views

import com.raquo.laminar.api.L._
import petclinic.views.components.{Button, ButtonConfig}
import petclinic.{Component, Style}

final case class HomeView() extends Component {
  val body: Div =
    div(
      cls("text-l text-justify m-5"),
      div(
        cls("mb-2 text-xl text-orange-700 font-bold"),
        "Welcome to ZIO Pet Clinic"
      ),
      div(
        cls("mb-4"),
        "A fullstack, idiomatic web app that serves as an example for best utilizing ZIO and the libraries within its ecosystem."
      ),
      div(
        cls("mb-4"),
        "Navigate to the Owners tab to preview a list of preloaded pet owners. Select an owner to do things like view their personal information, add pets, update visits for a pet, and create owners."
      ),
      div(
        cls("mb-2 text-xl text-gray-400 font-bold"),
        "Resources"
      ),
      div(
        bodyLink(
          "GitHub repo for this project",
          "https://github.com/zio/zio-petclinic"
        )
      ),
      div(
        bodyLink(
          "ZIO documentation",
          "https://zio.dev/"
        )
      )
    )

  private def bodyLink(name: String, url: String) =
    a(
      cls("text-gray-700 hover:text-gray-500 text-l cursor-pointer"),
      target("_blank"),
      name,
      href(url)
    )
}
