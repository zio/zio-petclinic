package petclinic

import com.raquo.laminar.api._
import com.raquo.waypoint._
import petclinic.models.OwnerId
import zio.json._

import java.util.UUID

sealed trait Page

object Page {
  case object OwnersPage                  extends Page
  final case class OwnerPage(id: OwnerId) extends Page
  case object HomePage                    extends Page
  case object VeterinariansPage           extends Page

  implicit val codec: JsonCodec[Page] = DeriveJsonCodec.gen[Page]
}

object Router {
  import Page._

  val homeRoute: Route[Page.HomePage.type, Unit] =
    Route.static(HomePage, root / endOfSegments)

  val veterinariansRoute: Route[Page.VeterinariansPage.type, Unit] =
    Route.static(VeterinariansPage, root / "veterinarians" / endOfSegments)

  val ownersRoute: Route[Page.OwnersPage.type, Unit] =
    Route.static(OwnersPage, root / "owners" / endOfSegments)

  val ownerRoute: Route[OwnerPage, String] = Route(
    encode = (userPage: Page.OwnerPage) => userPage.id.id.toString,
    decode = (id: String) => OwnerPage(OwnerId(UUID.fromString(id))),
    pattern = root / "owners" / segment[String] / endOfSegments
  )

  val router = new Router[Page](
    routes = List(ownersRoute, ownerRoute, homeRoute, veterinariansRoute),
    getPageTitle = _.toString,                                              // mock page title (displayed in the browser tab next to favicon)
    serializePage = page => page.toJson,                                    // serialize page data for storage in History API log
    deserializePage = pageStr => pageStr.fromJson[Page].getOrElse(HomePage) // deserialize the above
  )(
    popStateEvents = L.windowEvents(_.onPopState), // this is how Waypoint avoids an explicit dependency on Laminar
    owner = L.unsafeWindowOwner                    // this router will live as long as the window
  )
}
