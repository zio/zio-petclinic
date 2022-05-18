package petclinic

import com.raquo.laminar.api.L.{Owner => _, _}
import views._

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
