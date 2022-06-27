package petclinic.views

import com.raquo.laminar.api.L._
import petclinic.Component

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
        cls("mb-4"),
        bodyLink(
          "ZIO documentation",
          "https://zio.dev/"
        )
      )
//      div(
//        cls("mb-2 text-xl text-gray-400 font-bold"),
//        "Libraries Used"
//      ),
//      div(
//        bodyLink(
//          "Animus",
//          ""
//        )
//      ),
//      div(
//        bodyLink(
//          "Laminar",
//          ""
//        )
//      ),
//      div(
//        bodyLink(
//          "ZIO HTTP",
//          ""
//        )
//      ),
//      div(
//        bodyLink(
//          "ZIO JSON",
//          ""
//        )
//      ),
//      div(
//        bodyLink(
//          "ZIO Logging",
//          ""
//        )
//      ),
//      div(
//        bodyLink(
//          "ZIO Metrics",
//          ""
//        )
//      ),
//      div(
//        bodyLink(
//          "ZIO Quill",
//          ""
//        )
//      ),
//      div(
//        bodyLink(
//          "ZIO Test Containers",
//          ""
//        )
//      )
    )

  private def bodyLink(name: String, url: String) =
    a(
      cls("text-orange-700 hover:text-orange-600 text-l cursor-pointer"),
      target("_blank"),
      name,
      href(url)
    )
}
