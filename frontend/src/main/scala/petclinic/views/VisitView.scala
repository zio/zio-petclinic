package petclinic.views

import animus.Transitions
import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.models.{CreateVisit, Pet, UpdateVisit, Visit}
import petclinic.{Component, Requests}

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
        cls("text-sm text-gray-400 mb-1"),
        "Visits"
      ),
      children <-- $visits.map(_.map(EditableVisitView(_, () => refreshVisitsBus.emit(())))),
      div(
        NewVisitForm(pet, showNewVisitFormVar, () => refreshVisitsBus.emit(())),
        Transitions.heightDynamic(showNewVisitFormVar.signal)
      ),
      button(
        cls("text-sm cursor-pointer hover:text-gray-400 text-gray-500 underline"),
        "Add Visit",
        onClick --> { _ =>
          showNewVisitFormVar.set(true)
        },
        Transitions.height(showNewVisitFormVar.signal.map(!_))
      )
    )
}

final case class EditVisitForm(visit: Visit, showVar: Var[Boolean], reloadVisits: () => Unit) extends Component {
  val dateVar: Var[LocalDate]     = Var(visit.date)
  val descriptionVar: Var[String] = Var(visit.description)

  def body =
    div(
      cls("p-2 bg-gray-200 rounded-sm mb-4"),
      div(
        cls("text-sm text-gray-400 mb-1"),
        "Date"
      ),
      input(
        `type`("date"),
        background("none"),
        outline("none"),
        "text",
        placeholder("Date"),
        controlled(
          value <-- dateVar.signal.map(_.toString()),
          onInput.mapToValue --> { stringDate =>
            val localDate = LocalDate.parse(stringDate)
            dateVar.set(localDate)
            println(stringDate, localDate)
          }
        )
      ),
      div(
        cls("mt-4"),
        div(
          cls("text-sm text-gray-400 mb-1"),
          "Description"
        ),
        div(
          cls("mb-4"),
          input(
            focus <-- showVar.signal.changes,
            background("none"),
            outline("none"),
            "text",
            placeholder("Description of visit"),
            controlled(
              value <-- descriptionVar,
              onInput.mapToValue --> descriptionVar
            )
          )
        ),
        div(
          cls("flex items-center"),
          button(
            cls("p-2 px-4 text-red-500 border border-red-300 rounded-sm mr-2"),
            cls("hover:text-red-400"),
            "Delete",
            onClick --> { _ =>
              Requests
                .deleteVisit(visit.id)
                .foreach { _ =>
                  reloadVisits()
                }(unsafeWindowOwner)
              showVar.set(false)
            }
          ),
          button(
            cls("p-2 px-4 bg-gray-100 text-gray-500 border border-gray-300 rounded-sm mr-2"),
            cls("hover:text-gray-400"),
            "Cancel",
            onClick --> { _ =>
              dateVar.set(visit.date)
              descriptionVar.set(visit.description)
              showVar.set(false)
            }
          ),
          button(
            cls("p-2 px-4 text-orange-100 bg-orange-600 font-bold rounded-sm"),
            cls("hover:bg-orange-500"),
            "Save",
            onClick --> { _ =>
              val date        = dateVar.now()
              val description = descriptionVar.now()

              Requests
                .updateVisit(
                  visit.id,
                  UpdateVisit(
                    date = Some(date),
                    description = Some(description),
                    petId = visit.petId
                  )
                )
                .foreach { _ =>
                  reloadVisits()
                }(unsafeWindowOwner)

              showVar.set(false)
            }
          )
        )
      )
    )
}

final case class NewVisitForm(pet: Pet, showVar: Var[Boolean], refreshVisits: () => Unit) extends Component {
  val dateVar: Var[LocalDate]     = Var(LocalDate.now())
  val descriptionVar: Var[String] = Var("")

  def body =
    div(
      cls("p-2 bg-gray-200 rounded-sm"),
      div(
        cls("text-sm text-gray-400 mb-1"),
        "Date"
      ),
      input(
        cls("mb-4"),
        `type`("date"),
        background("none"),
        outline("none"),
        "text",
        placeholder("Date"),
        controlled(
          value <-- dateVar.signal.map(_.toString()),
          onInput.mapToValue --> { stringDate =>
            val localDate = LocalDate.parse(stringDate)
            dateVar.set(localDate)
            println(stringDate, localDate)
          }
        )
      ),
      div(
        cls("text-sm text-gray-400 mb-1"),
        "Description"
      ),
      div(
        cls("mb-4"),
        input(
          focus <-- showVar.signal.changes,
          background("none"),
          outline("none"),
          "text",
          placeholder("Description of visit"),
          controlled(
            value <-- descriptionVar,
            onInput.mapToValue --> descriptionVar
          )
        )
      ),
      div(
        cls("flex items-center"),
        Button(
          "Cancel",
          ButtonConfig.normal,
          { () =>
            dateVar.set(LocalDate.now())
            descriptionVar.set("")
            showVar.set(false)
          }
        ),
        Button(
          "Save",
          ButtonConfig.success,
          { () =>
            val date        = dateVar.now()
            val description = descriptionVar.now()

            Requests
              .addVisit(
                pet.id,
                CreateVisit(
                  date,
                  description
                )
              )
              .foreach { _ =>
                refreshVisits()
              }(unsafeWindowOwner)

            dateVar.set(LocalDate.now())
            descriptionVar.set("")
            showVar.set(false)
          }
        )
      )
    )
}

final case class EditableVisitView(visit: Visit, reloadVisits: () => Unit) extends Component {

  val isEditingVar = Var(false)

  def body =
    div(
      div(
        EditVisitForm(visit, isEditingVar, reloadVisits),
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
      cls("mb-4 p-2 bg-gray-200 rounded-sm"),
      div(
        cls("text-gray-500 text-sm"),
        cls("flex justify-between items-start"),
        div(
          div(Components.formatDate(visit.date)),
          child.text <-- $vet.map(_.lastName)
        ),
        button(
          cls("hover:text-gray-600 cursor-pointer"),
          "Edit",
          onClick --> { _ =>
            isEditingVar.set(true)
          }
        )
      ),
      div(
        visit.description
      )
    )
}
