package petclinic.views

import com.raquo.laminar.api.L._
import petclinic.models.api.{CreateVisit, UpdateVisit}
import petclinic.models.{PetId, Visit, api}
import petclinic.views.components.{Button, ButtonConfig}
import petclinic.{Component, Requests, Style}

import java.time.LocalDate

final case class VisitForm(
    petId: PetId,
    visit: Option[Visit],
    showVar: Var[Boolean],
    reloadVisits: () => Unit
) extends Component {
  val dateVar: Var[LocalDate]     = Var(LocalDate.now)
  val descriptionVar: Var[String] = Var("")

  val triedToSubmit: Var[Boolean]           = Var(false)
  val $triedToSubmit: StrictSignal[Boolean] = triedToSubmit.signal

  def resetVisit(): Unit = {
    dateVar.set(visit.map(_.date).getOrElse(LocalDate.now))
    descriptionVar.set(visit.map(_.description).getOrElse(""))
  }

  val $dateValidations: Signal[List[String]] =
    dateVar.signal.map(date =>
      if (date.isBefore(LocalDate.now)) List("Date cannot be in the past")
      else List.empty
    )

  val $descriptionValidations: Signal[List[String]] =
    descriptionVar.signal.map(description =>
      if (description.isEmpty) List("Please add a description")
      else List.empty
    )

  val $allValidations: Signal[List[String]] =
    $dateValidations.combineWithFn($descriptionValidations)((date, description) => date ++ description)

  val $isValid: Signal[Boolean] =
    $allValidations.map(_.isEmpty)

  def body: HtmlElement =
    form(
      onMountCallback { _ =>
        resetVisit()
      },
      cls("p-2 bg-gray-200 rounded-sm mb-4"),
      div(
        Style.header,
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
          Style.header,
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
          cls("flex items-center justify-end"),
          visit.map { visit =>
            div(
              cls("flex"),
              Button(
                "Delete",
                ButtonConfig.delete.small,
                { () =>
                  Requests
                    .deleteVisit(visit.id)
                    .foreach { _ =>
                      reloadVisits()
                    }(unsafeWindowOwner)
                  showVar.set(false)
                }
              ),
              div(cls("w-2"))
            )
          },
          Button(
            "Cancel",
            ButtonConfig.normal.small,
            { () =>
              resetVisit()
              showVar.set(false)
              triedToSubmit.set(false)
            }
          ),
          div(cls("w-2")),
          child <-- $isValid.map { isValid =>
            Button(
              "Save",
              ButtonConfig.success.small,
              () => if (isValid) handleSave() else triedToSubmit.set(true),
              isSubmit = true
            )
          }
        ),
        onSubmit --> { e =>
          e.preventDefault()
        }
      )
    )

  private def handleSave(): Unit = {
    triedToSubmit.set(false)

    if (showVar.now()) {
      val date        = dateVar.now()
      val description = descriptionVar.now()

      visit match {
        case Some(visit) =>
          Requests
            .updateVisit(
              visit.id,
              api.UpdateVisit(
                date = Some(date),
                description = Some(description),
                petId = visit.petId
              )
            )
            .foreach { _ =>
              reloadVisits()
            }(unsafeWindowOwner)
        case None =>
          Requests
            .addVisit(
              petId,
              CreateVisit(date, description)
            )
            .foreach { _ =>
              reloadVisits()
            }(unsafeWindowOwner)

      }
      showVar.set(false)
    }
  }
}
