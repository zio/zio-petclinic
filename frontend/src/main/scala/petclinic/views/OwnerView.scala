package petclinic.views

import animus._
import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.models._
import petclinic.views.components.{Button, ButtonConfig}
import petclinic.views.components.Components.labeled
import petclinic.{Style, _}

final case class EditableOwnerView(owner: Owner, reload: () => Unit) extends Component {
  val isEditingVar = Var(false)

  val body: Div =
    div(
      div(
        OwnerForm(Some(owner), isEditingVar, reload),
        Transitions.heightDynamic(isEditingVar.signal),
        Transitions.opacity(isEditingVar.signal)
      ),
      div(
        OwnerView(owner, isEditingVar),
        Transitions.heightDynamic(isEditingVar.signal.map(!_)),
        Transitions.opacity(isEditingVar.signal.map(!_))
      )
    )
}

final case class OwnerViewWrapper(ownerId: OwnerId) extends Component {

  val reloadOwnerBus =
    new EventBus[Unit]

  val $owner: EventStream[Owner] =
    EventStream.merge(
      Requests.getOwner(ownerId),
      reloadOwnerBus.events.flatMap { _ =>
        Requests.getOwner(ownerId)
      }
    )

  def body: Div = div(
    child <-- $owner.map(EditableOwnerView(_, () => reloadOwnerBus.emit(())))
  )
}

final case class OwnerView(owner: Owner, isEditingVar: Var[Boolean]) extends Component {

  val reloadPetBus: EventBus[Unit] =
    new EventBus[Unit]

  val reloadOwnerBus: EventBus[Unit] =
    new EventBus[Unit]

  val $pets: Signal[List[Pet]] =
    reloadPetBus.events
      .flatMap { _ =>
        Requests.getPets(owner.id)
      }
      .toSignal(List.empty)

  val $owner: EventStream[Owner] =
    reloadOwnerBus.events.flatMap { _ =>
      Requests.getOwner(owner.id)
    }

  val showNewPetFormVar   = Var(false)
  val showNewOwnerFormVar = Var(false)

  reloadPetBus.emit(())
  reloadOwnerBus.emit(())

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
      div(
        div(
          cls("flex"),
          Button("Add Pet", ButtonConfig.gray.small, () => showNewPetFormVar.update(!_)),
          div(cls("w-4")),
          Button("Edit", ButtonConfig.gray.small, () => isEditingVar.set(true))
        ),
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
      PetForm(owner.id, None, showNewPetFormVar, () => reloadPetBus.emit(()))
    ),
    div(cls("h-8")),
    children <-- $pets.map { pets =>
      pets.map(EditablePetView(_, () => reloadPetBus.emit(())))
    }
  )
}
