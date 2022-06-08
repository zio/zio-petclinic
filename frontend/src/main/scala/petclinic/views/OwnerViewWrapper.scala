package petclinic.views

import animus._
import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.models._
import petclinic.views.Components.labeled
import petclinic.{Style, _}

final case class EditOwnerForm(owner: Owner, showVar: Var[Boolean], reload: () => Unit) extends Component {
  val firstNameVar = Var(owner.firstName)
  val lastNameVar  = Var(owner.lastName)
  val emailVar     = Var(owner.email)
  val phoneVar     = Var(owner.phone)
  val addressVar   = Var(owner.address)

  def body =
    div(
      div(
        cls("flex"),
        div(
          div(
            cls("text-sm text-gray-400 mb-1"),
            "First Name"
          ),
          div(
            cls("mb-4"),
            input(
              focus <-- showVar.signal.changes,
              cls("text-2xl"),
              background("none"),
              outline("none"),
              Style.serifFont,
              "text",
              controlled(
                value <-- firstNameVar,
                onInput.mapToValue --> firstNameVar
              )
            )
          )
        ),
        div(
          div(
            cls("text-sm text-gray-400 mb-1"),
            "Last Name"
          ),
          div(
            cls("mb-4"),
            input(
              focus <-- showVar.signal.changes,
              cls("text-2xl"),
              background("none"),
              outline("none"),
              Style.serifFont,
              "text",
              controlled(
                value <-- lastNameVar,
                onInput.mapToValue --> lastNameVar
              )
            )
          )
        )
      ),
      div(
        div(
          div(
            div(
              cls("text-sm text-gray-400 mb-1"),
              "Email"
            ),
            div(
              cls("mb-4"),
              input(
                focus <-- showVar.signal.changes,
                background("none"),
                outline("none"),
                "text",
                placeholder("example@email.com"),
                controlled(
                  value <-- emailVar,
                  onInput.mapToValue --> emailVar
                )
              )
            )
          ),
          div(
            div(
              cls("text-sm text-gray-400 mb-1"),
              "Phone"
            ),
            div(
              cls("mb-4"),
              input(
                focus <-- showVar.signal.changes,
                background("none"),
                outline("none"),
                "text",
                placeholder("123-456-7890"),
                controlled(
                  value <-- phoneVar,
                  onInput.mapToValue --> phoneVar
                )
              )
            )
          ),
          div(
            div(
              cls("text-sm text-gray-400 mb-1"),
              "Address"
            ),
            div(
              cls("mb-4"),
              input(
                focus <-- showVar.signal.changes,
                background("none"),
                outline("none"),
                "text",
                placeholder("Address"),
                controlled(
                  value <-- addressVar,
                  onInput.mapToValue --> addressVar
                )
              )
            )
          )
        ),
        div(
          cls("flex items-center justify-end"),
          button(
            cls("p-2 px-4 text-red-500 border border-red-300 text-lg mr-6"),
            cls("hover:text-red-400"),
            "Delete",
            onClick --> { _ =>
              Requests
                .deleteOwner(owner.id)
                .foreach { _ =>
                  Router.router.pushState(Page.OwnersPage)
                }(unsafeWindowOwner)
              showVar.set(false)
            }
          ),
          button(
            cls("p-2 px-4 bg-gray-100 text-gray-500 border border-gray-300 text-lg rounded-sm mr-6"),
            cls("hover:text-gray-400"),
            "Cancel",
            onClick --> { _ =>
              firstNameVar.set(owner.firstName)
              lastNameVar.set(owner.lastName)
              emailVar.set(owner.email)
              phoneVar.set(owner.phone)
              addressVar.set(owner.address)
              showVar.set(false)
            }
          ),
          button(
            cls("p-2 px-4 text-orange-100 bg-orange-600 text-lg font-bold rounded-sm"),
            cls("hover:bg-orange-500"),
            "Save",
            onClick --> { _ =>
              val firstName = firstNameVar.now()
              val lastName  = lastNameVar.now()
              val email     = emailVar.now()
              val phone     = phoneVar.now()
              val address   = addressVar.now()

              Requests
                .updateOwner(
                  owner.id,
                  UpdateOwner(
                    firstName = Some(firstName),
                    lastName = Some(lastName),
                    address = Some(address),
                    phone = Some(phone),
                    email = Some(email)
                  )
                )
                .foreach { _ =>
                  reload()
                }(unsafeWindowOwner)

              firstNameVar.set("")
              lastNameVar.set("")
              emailVar.set("")
              phoneVar.set("")
              addressVar.set("")
              showVar.set(false)
            }
          )
        )
      )
    )
}

final case class EditableOwnerView(owner: Owner, reload: () => Unit) extends Component {

  val isEditingVar = Var(false)

  def body =
    div(
      div(
        EditOwnerForm(owner, isEditingVar, reload),
        Transitions.heightDynamic(isEditingVar.signal)
      ),
      div(
        OwnerView(owner, isEditingVar),
        Transitions.heightDynamic(isEditingVar.signal.map(!_))
      )
    )
}

case class OwnerViewWrapper(ownerId: OwnerId) extends Component {

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

case class OwnerView(owner: Owner, isEditingVar: Var[Boolean]) extends Component {

  val reloadPetBus: EventBus[Unit] =
    new EventBus[Unit]

  val reloadOwnerBus: EventBus[Unit] =
    new EventBus[Unit]

  val $pets: EventStream[List[Pet]] =
    reloadPetBus.events.flatMap { _ =>
      Requests.getPets(owner.id)
    }

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
        ),
        button(
          div(
            cls("p-1 px-2 rounded bg-gray-200 text-gray-500 mb-4 ml-4 hover:text-gray-400"),
            "Edit"
          ),
          onClick --> { _ =>
            isEditingVar.set(true)
          }
        )
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
      NewPetForm(owner, showNewPetFormVar, () => reloadPetBus.emit(()))
    ),
    div(cls("h-8")),
    children <-- $pets.map { pets =>
      pets.map(EditablePetView(_, () => reloadPetBus.emit(())))
    }
  )
}
