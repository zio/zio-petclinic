package petclinic

import com.raquo.laminar.api.L._
import petclinic.Page.HomePage
import petclinic.models.OwnerId

import java.util.UUID

final case class NavBar() extends Component {

  def body: Div =
    div(
      cls("flex items-center font-bold text-xl mb-12"),
      div("zio-clinic"),
      justifyContent.spaceBetween,
      div(
        textTransform.uppercase,
        display.flex,
        fontSize("18px"),
        navLink("Home", HomePage),
        navLink("Owners", Page.OwnerPage(OwnerId(UUID.fromString("941e3ad7-cb36-4f5a-ac6e-afc71666b36c")))),
        navLink("Veterinarians", Page.VeterinariansPage)
      )
    )

  private def navLink(text: String, page: Page): Div = {
    val $isActive =
      Router.router.$currentPage.map { currentPage =>
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
//      color <-- $isActive.map(if (_) "#452372" else "#929292"),
//      fontWeight <-- $isActive.map(if (_) "bold" else "normal")
    )
  }
}
