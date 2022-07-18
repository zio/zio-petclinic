package petclinic.views

import com.raquo.laminar.api.L._
import petclinic.models._
import petclinic.models.api.{CreatePet, UpdatePet}
import petclinic.views.components.{Button, ButtonConfig, Validations}
import petclinic.{Component, Requests, Style}

import java.time.LocalDate

final case class PetForm(
    ownerId: OwnerId,
    maybePet: Option[Pet],
    showVar: Var[Boolean],
    reloadPets: () => Unit
) extends Component {
  val petNameVar: Var[String]      = Var("")
  val speciesVar: Var[Species]     = Var(Species.Empty)
  val birthdateVar: Var[LocalDate] = Var(LocalDate.now())

  val triedToSubmit: Var[Boolean]           = Var(false)
  val $triedToSubmit: StrictSignal[Boolean] = triedToSubmit.signal

  def resetPet(): Unit = {
    petNameVar.set(maybePet.map(_.name).getOrElse(""))
    speciesVar.set(maybePet.map(_.species).getOrElse(Species.Empty))
    birthdateVar.set(maybePet.map(_.birthdate).getOrElse(LocalDate.now()))
  }

  val $petNameValidations: Signal[List[String]] =
    petNameVar.signal.map(petName => if (petName.isEmpty) List("Please enter a name") else List.empty)

  val $speciesValidations: Signal[List[String]] =
    speciesVar.signal.map(species => if (species == Species.Empty) List("Please select a species") else List.empty)

  val $birthdateValidations: Signal[List[String]] =
    birthdateVar.signal.map(birthdate =>
      if (birthdate.isAfter(LocalDate.now())) List("Date cannot be in the future")
      else List.empty
    )

  val $allValidations: Signal[List[String]] =
    $petNameValidations.combineWithFn($speciesValidations, $birthdateValidations) { (name, species, birthdate) =>
      name ++ species ++ birthdate
    }

  val $isValid: Signal[Boolean] =
    $allValidations.map(_.isEmpty)

  def body: HtmlElement =
    form(
      onMountCallback { _ =>
        resetPet()
      },
      cls("mt-4"),
      div(
        Style.header,
        "Pet Name"
      ),
      Validations($petNameValidations, $triedToSubmit),
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
              Style.header,
              "Species"
            ),
            Validations($speciesValidations, $triedToSubmit),
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
              Style.header,
              "Birthdate"
            ),
            Validations($birthdateValidations, $triedToSubmit),
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
              triedToSubmit.set(false)
            }
          ),
          div(cls("w-4")),
          child <-- $isValid.map { isValid =>
            Button(
              "Save",
              ButtonConfig.success,
              () => if (isValid) handleSave() else triedToSubmit.set(true),
              isSubmit = true
            )
          }
        ),
        div(cls("h-8")),
        onSubmit --> { e =>
          e.preventDefault()
        }
      ),
      div(cls("h-8")),
      div(
        height("1px"),
        background("#000000"),
        opacity(0.1)
      )
    )

  private def handleSave(): Unit = {
    triedToSubmit.set(false)

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
    }
  }
}
