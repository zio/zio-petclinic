package petclinic.views

import animus._
import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.{Component, Page, Requests, Router, Style}
import petclinic.models._
import petclinic.views.components.{Button, ButtonConfig}

final case class OwnerIndexView() extends Component {

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
      .map(_.reverse)
      .toSignal(List.empty)

  val searchQueryVar = Var("")

  val showNewOwnerFormVar = Var(false)

  val $owners: Signal[List[Owner]] =
    searchQueryVar.signal.combineWithFn($loadedOwners) { (query, owners) =>
      if (query.trim.isEmpty) owners
      else owners.filter(_.fullName.toLowerCase.contains(query.toLowerCase))
    }

  def body: Div =
    div(
      div(
        cls("flex items-start justify-between"),
        div(
          input(
            cls("p-1 mb-2 rounded cursor-text placeholder-gray-400 text-gray-600 border border-gray-200"),
            placeholder("Filter Owners"),
            outline("none"),
            controlled(
              value <-- searchQueryVar,
              onInput.mapToValue --> searchQueryVar
            )
          )
        ),
        Button(
          "Add Owner",
          ButtonConfig.gray.small,
          () => showNewOwnerFormVar.update(!_)
        ),
        overflowY.hidden,
        height <-- showNewOwnerFormVar.signal
          .map(!_)
          .map(if (_) 50.0 else 0.0)
          .spring
          .px,
        Transitions.opacity(showNewOwnerFormVar.signal.map(!_))
      ),
      div(
        OwnerForm(None, showNewOwnerFormVar, () => loadOwnersEventBus.emit(())),
        Transitions.height(showNewOwnerFormVar.signal),
        Transitions.opacity(showNewOwnerFormVar.signal)
      ),
      div(
        cls("text-sm text-gray-400 mb-2 mt-6"),
        "Recently Added Owners"
      ),
      children <-- $owners.splitTransition(_.id) { (_, owner, _, t) =>
        div(
          OwnerLinkView(owner),
          Transitions.heightDynamic(t.$isActive),
          t.opacity
        )
      }
    )
}

final case class OwnerLinkView(owner: Owner) extends Component {
  val appeared = Var(false)

  def body =
    div(
      div(
        onMountCallback { _ =>
          appeared.set(true)
        },
        cls("cursor-pointer p-4 mb-2 bg-gray-200 text-gray-600 text-lg rounded"),
        cls("hover:text-orange-500"),
        div(
          Style.serifFont,
          owner.fullName
        ),
        onClick --> { _ =>
          Router.router.pushState(Page.OwnerPage(owner.id))
        }
      )
    )

}
