package petclinic

import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.views._

object MainPage {
  def body: Div =
    div(
      cls("h-screen flex flex-col"),
      fontFamily("Inter, -apple-system, Segoe UI, Roboto, Noto Sans, Ubuntu, Cantarell, Helvetica Neue"),
      cls("text-gray-900"),
      NavBar(),
      div(
        cls("flex-grow sm:flex sm:justify-center"),
        div(
          cls("flex-grow max-w-screen-md"),
          child <-- Router.router.$currentPage.map {
            case Page.OwnersPage         => OwnerIndexView()
            case Page.OwnerPage(ownerId) => OwnerViewWrapper(ownerId)
            case Page.HomePage           => div("HOME")
            case Page.VeterinariansPage  => VetIndexView()
          }
        )
      ),
      Footer()
    )
}
