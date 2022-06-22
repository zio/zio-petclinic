package petclinic.views

import animus.Transitions
import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.models.api.{CreateVisit, UpdateVisit}
import petclinic.models.{Pet, Visit}
import petclinic.views.components.{Button, ButtonConfig, Components}
import petclinic.{Component, Requests, Style}

import java.time.LocalDate

final case class VisitsView(pet: Pet) extends Component {

  val refreshVisitsBus =
    new EventBus[Unit]

  val $visits: Signal[List[Visit]] =
    refreshVisitsBus.events
      .flatMap(_ => Requests.getVisits(pet.id))
      .toSignal(List.empty)

  val showNewVisitFormVar = Var(false)

  def body: Div =
    div(
      $visits --> { _ => () },
      onMountCallback { _ =>
        refreshVisitsBus.emit(())
      },
      div(
        Style.header,
        "Visits"
      ),
      children <-- $visits.map(_.map(EditableVisitView(_, () => refreshVisitsBus.emit(())))),
      div(
        VisitForm(pet.id, None, showNewVisitFormVar, () => refreshVisitsBus.emit(())),
        Transitions.heightDynamic(showNewVisitFormVar.signal)
      ),
      div(
        Button(
          "Add Visit",
          ButtonConfig.gray.small,
          () => showNewVisitFormVar.set(true)
        ),
        Transitions.height(showNewVisitFormVar.signal.map(!_))
      )
    )
}

final case class EditableVisitView(visit: Visit, reloadVisits: () => Unit) extends Component {

  val isEditingVar = Var(false)

  def body =
    div(
      div(
        VisitForm(visit.petId, Some(visit), isEditingVar, reloadVisits),
        Transitions.heightDynamic(isEditingVar.signal)
      ),
      div(
        VisitView(visit, isEditingVar),
        Transitions.heightDynamic(isEditingVar.signal.map(!_))
      )
    )
}

final case class VisitView(visit: Visit, isEditingVar: Var[Boolean] = Var(false)) extends Component {

  val $vet = Requests.getVet(visit.vetId)

  def body =
    div(
      cls("mb-2 p-1 pl-2 bg-gray-200 rounded-sm"),
      div(
        cls("text-gray-500 text-sm"),
        cls("flex justify-between items-start"),
        div(
          div(Components.formatDate(visit.date)),
          child.text <-- $vet.map(_.lastName)
        ),
        Button(
          "Edit",
          ButtonConfig.gray.small,
          () => isEditingVar.set(true)
        )
      ),
      div(
        visit.description
      )
    )
}
