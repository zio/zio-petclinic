package petclinic.views

import animus.Transitions
import com.raquo.laminar.api.L.{Owner => _, _}
import petclinic.models._
import petclinic.views.Components.labeled
import petclinic.{Component, Style}

final case class EditablePetView(pet: Pet, reloadPets: () => Unit) extends Component {
  val isEditingVar = Var(false)

  def body =
    div(
      div(
        PetForm(pet.ownerId, Some(pet), isEditingVar, reloadPets),
        Transitions.heightDynamic(isEditingVar.signal)
      ),
      div(
        PetView(pet, isEditingVar),
        Transitions.heightDynamic(isEditingVar.signal.map(!_))
      )
    )
}

final case class PetView(pet: Pet, isEditingVar: Var[Boolean]) extends Component {

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
