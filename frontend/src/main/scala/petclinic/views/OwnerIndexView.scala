package petclinic.views

import animus.Transitions
import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.{Component, Page, Requests, Router, Style}
import petclinic.models._

final case class NewOwnerForm(showVar: Var[Boolean], reload: () => Unit) extends Component {
  val firstNameVar: Var[String] = Var("")
  val lastNameVar: Var[String]  = Var("")
  val emailVar: Var[String]     = Var("")
  val addressVar: Var[String]   = Var("")
  val phoneVar: Var[String]     = Var("")

  def body = {
    div(
      cls("mt-4"),
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
          placeholder("First Name"),
          controlled(
            value <-- firstNameVar,
            onInput.mapToValue --> firstNameVar
          )
        )
      ),
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
          placeholder("Last Name"),
          controlled(
            value <-- lastNameVar,
            onInput.mapToValue --> lastNameVar
          )
        )
      ),
      div(
        cls("flex items-end justify-between"),
        div(
          cls("flex items-center"),
          div(
            cls("mr-8"),
            div(
              cls("text-sm text-gray-400 mb-1"),
              "Email"
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
                placeholder("example@email.com"),
                controlled(
                  value <-- emailVar,
                  onInput.mapToValue --> emailVar
                )
              )
            ),
            div(
              cls("text-sm text-gray-400 mb-1"),
              "Phone"
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
                placeholder("123-456-7890"),
                controlled(
                  value <-- phoneVar,
                  onInput.mapToValue --> phoneVar
                )
              )
            ),
            div(
              cls("text-sm text-gray-400 mb-1"),
              "Address"
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
          cls("flex items-center"),
          button(
            cls("p-2 px-4 bg-gray-100 text-gray-500 border border-gray-300 text-lg rounded-sm mr-6"),
            cls("hover:text-gray-400"),
            "Cancel",
            onClick --> { _ =>
              firstNameVar.set("")
              lastNameVar.set("")
              emailVar.set("")
              phoneVar.set("")
              addressVar.set("")
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
                .addOwner(
                  CreateOwner(
                    firstName,
                    lastName,
                    email,
                    phone,
                    address
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
      ),
      div(cls("h-8")),
      div(
        height("1px"),
        background("#000000"),
        opacity(0.1)
      )
    )
  }
}

case class OwnerIndexView() extends Component {

  val loadOwnersEventBus =
    new EventBus[Unit]

  val $loadedOwners: Signal[List[Owner]] =
    EventStream
      .merge(
        Requests.getAllOwners,
        loadOwnersEventBus.events
          .flatMap { _ =>
            Requests.getAllOwners
          }
      )
      .toSignal(List.empty)

  val searchQueryVar = Var("")

  val showNewOwnerFormVar = Var(false)

  val $owners: Signal[List[Owner]] =
    searchQueryVar.signal.combineWithFn($loadedOwners) { (query, owners) =>
      if (query.isBlank) owners
      else owners.filter(_.fullName.toLowerCase.contains(query.toLowerCase))
    }

  def body =
    div(
      button(
        div(
          cls("p-1 px-2 rounded bg-gray-200 text-gray-500 mb-4 hover:text-gray-400"),
          "Add Owner"
        ),
        onClick --> { _ =>
          showNewOwnerFormVar.update(!_)
        },
        Transitions.height(showNewOwnerFormVar.signal.map(!_)),
        Transitions.opacity(showNewOwnerFormVar.signal.map(!_)),
        NewOwnerForm(showNewOwnerFormVar, () => loadOwnersEventBus.emit(()))
      ),
      div(
        input(
          cls("p-1 mb-2 rounded cursor-text placeholder-gray-300 text-gray-600"),
          placeholder("Search Owners"),
          controlled(
            value <-- searchQueryVar,
            onInput.mapToValue --> searchQueryVar
          )
        ),
        button(
          div(
            cls("p-1 px-2 rounded bg-gray-200 text-gray-500 mb-4 ml-4 hover:text-gray-400"),
            "Search"
          )
          //        onClick --> { _ =>
          //          showNewOwnerFormVar.update(!_)
          //        },
          //        Transitions.height(showNewOwnerFormVar.signal.map(!_)),
          //        Transitions.opacity(showNewOwnerFormVar.signal.map(!_))
        )
      ),
      div(
        cls("text-sm text-gray-400 mb-2 mt-6"),
        "Recently Added Owners"
      ),
      children <-- $owners.split(_.id) { (_, owner, _) =>
        div(
          cls("cursor-pointer p-4 mb-2 bg-gray-200 text-gray-500 rounded"),
          div(
            owner.fullName
          ),
          onClick --> { _ =>
            Router.router.pushState(Page.OwnerPage(owner.id))
          }
        )
      }
    )
}
