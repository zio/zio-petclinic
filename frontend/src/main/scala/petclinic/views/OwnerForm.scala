package petclinic.views

import petclinic.models.Owner
import com.raquo.laminar.api.L._
import petclinic.views.components._
import petclinic.models.api.{CreateOwner, UpdateOwner}
import petclinic.{Component, Page, Requests, Router, Style}

final case class OwnerForm(maybeOwner: Option[Owner], showVar: Var[Boolean], reloadOwner: () => Unit)
    extends Component {
  val firstNameVar: Var[String] = Var("")
  val lastNameVar: Var[String]  = Var("")
  val emailVar: Var[String]     = Var("")
  val phoneVar: Var[String]     = Var("")
  val addressVar: Var[String]   = Var("")

  val triedToSubmit: Var[Boolean]           = Var(false)
  val $triedToSubmit: StrictSignal[Boolean] = triedToSubmit.signal

  val $firstNameValidations: Signal[List[String]] =
    firstNameVar.signal.map(firstName => if (firstName.isEmpty) List("Please enter a first name") else List.empty)

  val $lastNameValidations: Signal[List[String]] =
    lastNameVar.signal.map(lastName => if (lastName.isEmpty) List("Please enter a last name") else List.empty)

  val $emailValidations: Signal[List[String]] =
    emailVar.signal.map { email =>
      if (email.isEmpty) List("Please enter an email address")
      else if ("""^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$""".r.unapplySeq(email).isDefined) List.empty
      else List("Invalid email")
    }

  val $phoneValidations: Signal[List[String]] =
    phoneVar.signal.map { phone =>
      if (phone.isEmpty) List("Please enter a phone number")
      else if ("""^\d{3}-\d{3}-\d{4}$""".r.unapplySeq(phone).isDefined) List.empty
      else List("Invalid phone number")
    }

  val $addressValidations: Signal[List[String]] =
    addressVar.signal.map { address =>
      if (address.isEmpty) List("Please enter an address")
      else List.empty
    }

  val $allValidations: Signal[List[String]] =
    $firstNameValidations.combineWithFn(
      $lastNameValidations,
      $emailValidations,
      $phoneValidations,
      $addressValidations
    ) { (firstName, lastName, email, phone, address) =>
      firstName ++ lastName ++ email ++ phone ++ address
    }

  val $isValid: Signal[Boolean] =
    $allValidations.map(_.isEmpty)

  def resetOwner(): Unit = {
    firstNameVar.set(maybeOwner.map(_.firstName).getOrElse(""))
    lastNameVar.set(maybeOwner.map(_.lastName).getOrElse(""))
    emailVar.set(maybeOwner.map(_.email).getOrElse(""))
    phoneVar.set(maybeOwner.map(_.phone).getOrElse(""))
    addressVar.set(maybeOwner.map(_.address).getOrElse(""))
  }

  def body: HtmlElement =
    form(
      onMountCallback { _ =>
        resetOwner()
      },
      div(
        cls("flex"),
        div(
          div(
            Style.header,
            "First Name"
          ),
          Validations($firstNameValidations, $triedToSubmit),
          div(
            cls("mb-4"),
            input(
              focus <-- showVar.signal.changes,
              placeholder("First Name"),
              cls("text-2xl"),
              background("none"),
              outline("none"),
              Style.serifFont,
              "text",
              controlled(
                value <-- firstNameVar,
                onInput.mapToValue --> firstNameVar
              )
            )
          )
        ),
        div(
          div(
            Style.header,
            "Last Name"
          ),
          Validations($lastNameValidations, $triedToSubmit),
          div(
            cls("mb-4"),
            input(
              placeholder("Last Name"),
              cls("text-2xl"),
              background("none"),
              outline("none"),
              Style.serifFont,
              "text",
              controlled(
                value <-- lastNameVar,
                onInput.mapToValue --> lastNameVar
              )
            )
          )
        )
      ),
      div(
        div(
          div(
            div(
              Style.header,
              "Email"
            ),
            Validations($emailValidations, $triedToSubmit),
            div(
              cls("mb-4"),
              input(
                placeholder("Email"),
                background("none"),
                outline("none"),
                "text",
                placeholder("example@email.com"),
                controlled(
                  value <-- emailVar,
                  onInput.mapToValue --> emailVar
                )
              )
            )
          ),
          div(
            div(
              Style.header,
              "Phone"
            ),
            Validations($phoneValidations, $triedToSubmit),
            div(
              cls("mb-4"),
              input(
                placeholder("Phone"),
                background("none"),
                outline("none"),
                "text",
                placeholder("123-456-7890"),
                controlled(
                  value <-- phoneVar,
                  onInput.mapToValue --> phoneVar
                )
              )
            )
          ),
          div(
            div(
              Style.header,
              "Address"
            ),
            Validations($addressValidations, $triedToSubmit),
            div(
              cls("mb-4"),
              input(
                placeholder("Address"),
                background("none"),
                outline("none"),
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
          cls("flex items-center justify-end"),
          maybeOwner.map { owner =>
            div(
              cls("flex"),
              Button(
                "Delete",
                ButtonConfig.delete,
                { () =>
                  Requests
                    .deleteOwner(owner.id)
                    .foreach { _ =>
                      Router.router.pushState(Page.OwnersPage)
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
              resetOwner()
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
        onSubmit --> { e =>
          e.preventDefault()
        }
      )
    )

  private def handleSave(): Unit = {
    triedToSubmit.set(false)

    if (!showVar.now())
      return

    val firstName = firstNameVar.now()
    val lastName  = lastNameVar.now()
    val email     = emailVar.now()
    val phone     = phoneVar.now()
    val address   = addressVar.now()

    maybeOwner match {
      case Some(owner) =>
        Requests
          .updateOwner(
            owner.id,
            UpdateOwner(
              firstName = Some(firstName),
              lastName = Some(lastName),
              address = Some(address),
              phone = Some(phone),
              email = Some(email)
            )
          )
          .foreach { _ =>
            reloadOwner()
          }(unsafeWindowOwner)
      case None =>
        Requests
          .addOwner(
            CreateOwner(
              firstName = firstName,
              lastName = lastName,
              address = address,
              phone = phone,
              email = email
            )
          )
          .foreach { - =>
            reloadOwner()
          }(unsafeWindowOwner)
    }
    showVar.set(false)
  }
}
