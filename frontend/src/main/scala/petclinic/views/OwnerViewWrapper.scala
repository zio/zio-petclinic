package petclinic.views

import animus._
import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.models._
import petclinic.views.Components.labeled
import petclinic.{Component, Requests, Style}

import java.time.LocalDate

case class OwnerViewWrapper(ownerId: OwnerId) extends Component {
  val $owner: EventStream[Owner] = Requests.getOwner(ownerId)

  def body: Div = div(
    child <-- $owner.map(OwnerView(_))
  )
}

case class OwnerView(owner: Owner) extends Component {

  val reloadPetBus: EventBus[Unit] =
    new EventBus[Unit]

  val $pets: EventStream[List[Pet]] =
    reloadPetBus.events.flatMap { _ =>
      Requests.getPets(owner.id)
    }

  val showNewPetFormVar = Var(false)

  reloadPetBus.emit(())

  def body: Div = div(
    reloadPetBus.events --> { _ => () },
    onMountCallback(_ => reloadPetBus.emit(())),
    div(
      cls("flex justify-between items-start"),
      div(
        div(
          cls("text-sm text-gray-400 mb-1"),
          "Owner"
        ),
        div(
          cls("text-4xl text-orange-600 mb-4"),
          Style.serifFont,
          s"${owner.firstName} ${owner.lastName}"
        )
      ),
      button(
        div(
          cls("p-1 px-2 rounded bg-gray-200 text-gray-500 mb-4 hover:text-gray-400"),
          "Add Pet"
        ),
        onClick --> { _ =>
          showNewPetFormVar.update(!_)
        },
        Transitions.height(showNewPetFormVar.signal.map(!_)),
        Transitions.opacity(showNewPetFormVar.signal.map(!_))
      )
    ),
    div(
      display.flex,
      labeled("Email", owner.email),
      labeled("Phone", owner.phone),
      labeled("Address", owner.address)
    ),
    div(cls("h-8")),
    div(
      height("1px"),
      background("#000000"),
      opacity(0.1)
    ),
    div(
      Transitions.height(showNewPetFormVar.signal),
      Transitions.opacity(showNewPetFormVar.signal),
      NewPetForm(owner, showNewPetFormVar)
    ),
    div(cls("h-8")),
    children <-- $pets.map { pets =>
      pets.map(EditablePetView(_, () => reloadPetBus.emit(())))
    }
  )
}

object Components {

  def labeled(name: String, value: String): Div =
    div(
      cls("mr-8"),
      div(
        cls("text-sm text-gray-400 mb-1"),
        name
      ),
      div(value)
    )

  // format date like May 22, 1990
  def formatDate(localDate: LocalDate): String = {
    val month = localDate.getMonth.toString.toLowerCase.capitalize
    val day   = localDate.getDayOfMonth
    val year  = localDate.getYear
    s"$month $day, $year"
  }

}
