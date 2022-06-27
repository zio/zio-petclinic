package petclinic.views.components

import animus._
import petclinic.Component
import com.raquo.laminar.api.L._

final case class Validations($validations: Signal[List[String]], $triedToSubmit: Signal[Boolean]) extends Component {
  def body: Div =
    div(
      children <-- $validations.combineWithFn($triedToSubmit) { (validations, submitted) =>
        if (!submitted) List.empty[String]
        else validations
      }.splitTransition(identity) { (_, string, _, t) =>
        div(
          cls("text-red-500 text-sm"),
          string,
          t.height
        )
      }
    )

}
