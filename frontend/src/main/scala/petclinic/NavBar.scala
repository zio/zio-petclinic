package petclinic

import com.raquo.laminar.api.L._
import petclinic.Page._

final case class NavBar() extends Component {

  def body: Div =
    div(
      cls("flex items-center mb-12 p-8"),
      button(
        cls("flex"),
        img(
          src("https://raw.githubusercontent.com/zio/zio/master/ZIO.png"),
          height("30px")
        ),
        div(cls("text-gray-700 font-bold text-xl"), "Pet Clinic"),
        onClick --> { _ =>
          Router.router.pushState(Page.HomePage)
        }
      ),
      justifyContent.spaceBetween,
      div(
        textTransform.uppercase,
        display.flex,
        fontSize("18px"),
        navLink("Home", HomePage),
        navLink("Owners", OwnersPage),
        navLink("Vets", VeterinariansPage)
      )
    )

  private def navLink(text: String, page: Page): Div = {
    val $isActive =
      Router.router.currentPageSignal.map { currentPage =>
        currentPage == page
      }
    div(
      cls("ml-6"),
      cursor.pointer,
      text,
      onClick --> { _ =>
        Router.router.pushState(page)
      },
      cls <-- $isActive.map {
        case true  => "text-orange-700 font-bold hover:text-orange-600"
        case false => "text-gray-500 font-normal hover:text-orange-600"
      }
    )
  }
}
