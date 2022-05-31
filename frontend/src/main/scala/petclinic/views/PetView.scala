package petclinic.views

import animus.Transitions
import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.{Component, Requests, Style}
import petclinic.models._
import petclinic.views.Components.labeled

import java.time.LocalDate

final case class EditablePetView(pet: Pet, reloadPets: () => Unit) extends Component {

  val isEditingVar = Var(false)

  def body =
    div(
      div(
        EditPetForm(pet, isEditingVar, reloadPets),
        Transitions.heightDynamic(isEditingVar.signal)
      ),
      div(
        PetView(pet, isEditingVar),
        Transitions.heightDynamic(isEditingVar.signal.map(!_))
      ),
      div(
        // add visit form
      )
    )
}

final case class PetView(pet: Pet, isEditingVar: Var[Boolean]) extends Component {

  // pet and its visits
  // visit (date, description, and the vet name)
  def body =
    div(
      cls("mb-20"),
      div(
        cls("grid grid-cols-2"),
        div(
          div(
            cls("text-sm text-gray-400 mb-1"),
            "Pet Name"
          ),
          div(
            cls("text-2xl mb-4"),
            Style.serifFont,
            pet.name
          ),
          div(
            cls("flex mb-4"),
            labeled("Species", pet.species.toString),
            labeled("Birthdate", Components.formatDate(pet.birthdate))
          ),
          div(
            cls("text-sm cursor-pointer hover:text-gray-400 text-gray-500 underline"),
            "Edit Pet",
            onClick --> { _ =>
              isEditingVar.set(true)
            }
          )
        ),
        VisitsView(pet)
      )
    )

}

final case class EditPetForm(
    pet: Pet,
    showVar: Var[Boolean],
    reloadPets: () => Unit
) extends Component {
  val petNameVar: Var[String]      = Var(pet.name)
  val speciesVar: Var[Species]     = Var(pet.species)
  val birthdateVar: Var[LocalDate] = Var(pet.birthdate)

  def body =
    div(
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
          button(
            cls("p-2 px-4 bg-gray-100 text-gray-500 border border-gray-300 text-lg rounded-sm mr-6"),
            cls("hover:text-gray-400"),
            "Cancel",
            onClick --> { _ =>
              petNameVar.set(pet.name)
              speciesVar.set(pet.species)
              birthdateVar.set(pet.birthdate)
              showVar.set(false)
            }
          ),
          button(
            cls("p-2 px-4 text-orange-100 bg-orange-600 text-lg font-bold rounded-sm"),
            cls("hover:bg-orange-500"),
            "Save",
            onClick --> { _ =>
              val name      = petNameVar.now()
              val species   = speciesVar.now()
              val birthdate = birthdateVar.now()

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

final case class NewPetForm(
    owner: Owner,
    showVar: Var[Boolean]
) extends Component {
  val petNameVar: Var[String]      = Var("")
  val speciesVar: Var[Species]     = Var(Species.Feline)
  val birthdateVar: Var[LocalDate] = Var(LocalDate.now())

  def body = {
    div(
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
          button(
            cls("p-2 px-4 bg-gray-100 text-gray-500 border border-gray-300 text-lg rounded-sm mr-6"),
            cls("hover:text-gray-400"),
            "Cancel",
            onClick --> { _ =>
              petNameVar.set("")
              speciesVar.set(Species.Feline)
              birthdateVar.set(LocalDate.now())
              showVar.set(false)
            }
          ),
          button(
            cls("p-2 px-4 text-orange-100 bg-orange-600 text-lg font-bold rounded-sm"),
            cls("hover:bg-orange-500"),
            "Save",
            onClick --> { _ =>
              val name      = petNameVar.now()
              val species   = speciesVar.now()
              val birthdate = birthdateVar.now()

              Requests.addPet(
                CreatePet(
                  name,
                  birthdate,
                  species,
                  owner.id
                )
              )

              petNameVar.set("")
              speciesVar.set(Species.Feline)
              birthdateVar.set(LocalDate.now())
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
