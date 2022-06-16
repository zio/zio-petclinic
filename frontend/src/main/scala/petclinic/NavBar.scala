package petclinic

import com.raquo.laminar.api.L._

final case class NavBar() extends Component {

  def body: Div =
    div(
      cls("flex items-center font-bold text-xl mb-12 p-8"),
      div(
        cls("flex"),
        img(
          src("https://raw.githubusercontent.com/zio/zio/master/ZIO.png"),
          height("30px")
        ),
        div(cls("text-gray-700"), "Pet Clinic")
      ),
      justifyContent.spaceBetween,
      div(
        textTransform.uppercase,
        display.flex,
        fontSize("18px"),
        navLink("Owners", Page.OwnersPage),
        navLink("Vets", Page.VeterinariansPage)
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
