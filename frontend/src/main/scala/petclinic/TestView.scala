package petclinic
import com.raquo.laminar.api.L._

object TestView extends Component {
  val textVar    = Var("")
  val checkedVar = Var(false)

  override def body: HtmlElement =
    div(
      div(
        form("Enter your name"),
        input(onInput.mapToValue --> textVar)
      ),
      div(
        label("Your name is: ", child.text <-- textVar)
      ),
      div(
        label("Check box to verify "),
        input(
          typ("checkbox"),
          onInput.mapToChecked --> checkedVar
        )
      ),
      div(
        textArea(placeholder("What's your favorite color?"))
      ),
      div(
        button("submit")
      )
    )
}

// val inputTextVar = Var("")
//
//val checkedVar = Var(false)
//
//val app = div(
//  p(
//    label("Name: "),
//    input(
//      onInput.mapToValue --> inputTextVar
//    )
//  ),
//  p(
//    "You typed: ",
//    child.text <-- inputTextVar
//  ),
//  p(
//    label("I like to check boxes: "),
//    input(
//      typ("checkbox"),
//      onInput.mapToChecked --> checkedVar
//    )
//  ),
//  p(
//    "You checked the box: ",
//    child.text <-- checkedVar
//  )
//)
//
//render(containerNode, app)
