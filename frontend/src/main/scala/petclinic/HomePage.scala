package petclinic

import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.views._

// TODO LIST
//  √ Delete Visits (visit page)
//  √ Delete Pets (pet page)
//  √ Owner Index Page (List Recent Owners and show Search Bar) (owner homepage)
//  √ Search Owners by Name (owner homepage)
//  - Create Vet Index (vet page)
//  - Create Owners (owner homepage)
//  - Edit Owner (owner page)
//  - Delete Owner (owner page)

object MainPage {
  def body: Div =
    div(
      minHeight("100vh"),
      cls("bg-gray-100"),
      div(
        fontFamily("Inter, -apple-system, Segoe UI, Roboto, Noto Sans, Ubuntu, Cantarell, Helvetica Neue"),
        cls("p-8 text-gray-900"),
        NavBar(),
        div(
          maxWidth("750px"),
          margin("0 auto"),
          child <-- Router.router.$currentPage.map {
            case Page.OwnersPage         => OwnerIndexView()
            case Page.OwnerPage(ownerId) => OwnerViewWrapper(ownerId)
            case Page.HomePage           => div("HOME")
            case Page.VeterinariansPage  => div("VETS")
          }
        )
      )
    )
}
