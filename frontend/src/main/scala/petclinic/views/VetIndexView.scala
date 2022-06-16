package petclinic.views

import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.{Component, Page, Requests, Router}
import petclinic.models._

case class VetIndexView() extends Component {

  val $vets: Signal[List[Vet]] =
    Requests.getAllVets.toSignal(List.empty)

  def body =
    div(
      cls("w-full"),
      table(
        cls("table-auto w-full"),
        thead(
          tr(
            cls("text-left text-gray-500 text-sm"),
            th(cls("p-2 font-normal"), "Name"),
            th(cls("p-2 font-normal"), "Speciality")
          )
        ),
        tbody(
          children <-- $vets.split(_.id) { (_, vet, _) =>
            tr(
              td(cls("p-2 font-medium"), vet.lastName),
              td(cls("p-2"), vet.specialty)
            )
          }
        )
      )
    )
}
