package petclinic.views

import com.raquo.laminar.api.L._
import petclinic.models._
import petclinic.views.components.{Button, ButtonConfig}
import petclinic.{Component, Requests, Style}

import java.time.LocalDate

final case class PetForm(
    ownerId: OwnerId,
    maybePet: Option[Pet],
    showVar: Var[Boolean],
    reloadPets: () => Unit
) extends Component {
  val petNameVar: Var[String]      = Var("")
  val speciesVar: Var[Species]     = Var(Species.Feline)
  val birthdateVar: Var[LocalDate] = Var(LocalDate.now())

  def resetPet(): Unit = {
    petNameVar.set(maybePet.map(_.name).getOrElse(""))
    speciesVar.set(maybePet.map(_.species).getOrElse(Species.Feline))
    birthdateVar.set(maybePet.map(_.birthdate).getOrElse(LocalDate.now()))
  }

  def body: HtmlElement =
    form(
      onMountCallback { _ =>
        resetPet()
      },
      cls("mt-4"),
      div(
        cls("text-sm text-gray-400 mb-1"),
        "Pet Name"
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
          placeholder("Pet Name"),
          controlled(
            value <-- petNameVar,
            onInput.mapToValue --> petNameVar
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
              "Species"
            ),
            div(
              select(
                controlled(
                  value <-- speciesVar.signal.map(_.name),
                  onChange.mapToValue --> { value =>
                    speciesVar.set(Species.fromString(value))
                  }
                ),
                Species.all.map(species => option(species.name))
              )
            )
          ),
          div(
            div(
              cls("text-sm text-gray-400 mb-1"),
              "Birthdate"
            ),
            input(
              `type`("date"),
              background("none"),
              outline("none"),
              "text",
              placeholder("Birthdate"),
              controlled(
                value <-- birthdateVar.signal.map(_.toString()),
                onInput.mapToValue --> { stringDate =>
                  val localDate = LocalDate.parse(stringDate)
                  birthdateVar.set(localDate)
                  println(stringDate, localDate)
                }
              )
            )
          )
        ),
        div(
          cls("flex items-center"),
          maybePet.map { pet =>
            div(
              cls("flex"),
              Button(
                "Delete",
                ButtonConfig.delete,
                { () =>
                  Requests
                    .deletePet(pet.id)
                    .foreach { _ =>
                      reloadPets()
                    }(unsafeWindowOwner)
                  showVar.set(false)
                }
              ),
              div(cls("w-4"))
            )
          },
          Button(
            "Cancel",
            ButtonConfig.normal,
            { () =>
              resetPet()
              showVar.set(false)
            }
          ),
          div(cls("w-4")),
          Button(
            "Save",
            ButtonConfig.success,
            () =>
              if (showVar.now()) {
                val name      = petNameVar.now()
                val species   = speciesVar.now()
                val birthdate = birthdateVar.now()

                maybePet match {
                  case Some(pet) =>
                    Requests
                      .updatePet(
                        pet.id,
                        UpdatePet(
                          name = Some(name),
                          birthdate = Some(birthdate),
                          species = Some(species),
                          ownerId = None
                        )
                      )
                      .foreach { _ =>
                        reloadPets()
                      }(unsafeWindowOwner)
                  case None =>
                    Requests
                      .addPet(
                        CreatePet(
                          name,
                          birthdate,
                          species,
                          ownerId
                        )
                      )
                      .foreach { _ =>
                        reloadPets()
                      }(unsafeWindowOwner)

                }

                showVar.set(false)
              },
            isSubmit = true
          )
        )
      ),
      div(cls("h-8")),
      div(
        height("1px"),
        background("#000000"),
        opacity(0.1)
      ),
      div(cls("h-8")),
      onSubmit --> { e =>
        e.preventDefault()
      }
    )
}
