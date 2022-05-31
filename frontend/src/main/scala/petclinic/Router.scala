package petclinic

import com.raquo.laminar.api.L
import com.raquo.waypoint._
import petclinic.models.OwnerId
import zio.json._

import java.util.UUID

sealed trait Page

object Page {
  case class OwnerPage(id: OwnerId) extends Page
  case object HomePage              extends Page
  case object VeterinariansPage     extends Page

  implicit val codec: JsonCodec[Page] = DeriveJsonCodec.gen[Page]
}

object Router {
  import Page._

  val ownerRoute = Route(
    encode = (userPage: Page.OwnerPage) => userPage.id.id.toString,
    decode = (id: String) => OwnerPage(OwnerId(UUID.fromString(id))),
    pattern = root / "owners" / segment[String] / endOfSegments
  )

  val loginRoute =
    Route.static(HomePage, root / "login" / endOfSegments)

  val router = new Router[Page](
    routes = List(ownerRoute, loginRoute),
    getPageTitle = _.toString,                                              // mock page title (displayed in the browser tab next to favicon)
    serializePage = page => page.toJson,                                    // serialize page data for storage in History API log
    deserializePage = pageStr => pageStr.fromJson[Page].getOrElse(HomePage) // deserialize the above
  )(
    $popStateEvent = L.windowEvents.onPopState, // this is how Waypoint avoids an explicit dependency on Laminar
    owner = L.unsafeWindowOwner                 // this router will live as long as the window
  )
}
