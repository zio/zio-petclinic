package petclinic

import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.views._

// TODO LIST
//  √ Edit Visits
//  - Create Vet Index
//  - Delete Visits
//  - Delete Pets
//  - Create Owners
//  - Edit Owner
//  - Delete Owner
//  - Search Owners by Name
//  - Owner Index Page (List Recent Owners and show Search Bar)

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
            case Page.OwnerPage(ownerId) => OwnerViewWrapper(ownerId)
            case Page.HomePage           => div("LOGIN")
          }
        )
      )
    )
}
