package petclinic

import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.models._
import sttp.client3._

import scala.concurrent.Future
import zio.json._

object Requests {
  val backend = FetchBackend()

  def allOwners(): EventStream[List[Owner]] = {
    val request                          = quickRequest.get(uri"http://localhost:8080/owners")
    val future: Future[Response[String]] = backend.send(request)
    EventStream.fromFuture(future).map { response =>
      response.body.fromJson[List[Owner]] match {
        case Right(pets) => pets
        case Left(error) => throw new Error(s"Error parsing JSON: $error")
      }
    }
  }
}

trait Component {
  def body: HtmlElement
}

object Component {
  implicit def component2HtmlElement(component: Component): HtmlElement =
    component.body
}

case class OwnerView(owner: Owner) extends Component {
  def body = div(
    div(s"${owner.firstName} ${owner.lastName}"),
    div(owner.address)
  )
}

object HomePage {
  def body =
    div(
      cls("text-4xl text-red-300"),
      h1("Home"),
      pre(
        children <-- Requests.allOwners().map { owners =>
          owners.map(OwnerView(_))
        }
      )
    )
}
