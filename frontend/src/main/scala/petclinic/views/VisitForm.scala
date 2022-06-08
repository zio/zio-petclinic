package petclinic.views

import com.raquo.laminar.api.L._
import petclinic.models.{CreateVisit, PetId, UpdateVisit, Visit}
import petclinic.views.components.{Button, ButtonConfig}
import petclinic.{Component, Requests}

import java.time.LocalDate

final case class VisitForm(
    petId: PetId,
    visit: Option[Visit],
    showVar: Var[Boolean],
    reloadVisits: () => Unit
) extends Component {
  val dateVar: Var[LocalDate]     = Var(LocalDate.now)
  val descriptionVar: Var[String] = Var("")

  def resetVisit(): Unit = {
    dateVar.set(visit.map(_.date).getOrElse(LocalDate.now))
    descriptionVar.set(visit.map(_.description).getOrElse(""))
  }

  def body: HtmlElement =
    form(
      onMountCallback { _ =>
        resetVisit()
      },
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
            }
          ),
          div(cls("w-2")),
          Button(
            "Save",
            ButtonConfig.success.small,
            () =>
              if (showVar.now()) {
                val date        = dateVar.now()
                val description = descriptionVar.now()

                visit match {
                  case Some(visit) =>
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
              },
            isSubmit = true
          )
        )
      ),
      onSubmit --> { e =>
        e.preventDefault()
      }
    )
}
