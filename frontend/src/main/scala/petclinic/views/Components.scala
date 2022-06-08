package petclinic.views

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base
import petclinic.Component

import java.time.LocalDate

sealed trait ButtonStyle extends Product with Serializable { self =>
  def classes: Mod[HtmlElement] =
    self match {
      case ButtonStyle.Delete =>
        cls("border text-red-500 border-red-300 hover:text-red-400")
      case ButtonStyle.Gray =>
        cls("text-gray-500 bg-gray-200 hover:text-gray-400 rounded")
      case ButtonStyle.Normal =>
        cls("border text-gray-500 border-gray-300 hover:text-gray-400")
      case ButtonStyle.Success =>
        cls("bg-orange-600 text-orange-200 hover:text-orange-100 hover:bg-orange-500 font-medium")
    }
}

object ButtonStyle {
  case object Delete  extends ButtonStyle
  case object Normal  extends ButtonStyle
  case object Gray    extends ButtonStyle
  case object Success extends ButtonStyle
}

final case class ButtonConfig(style: ButtonStyle, size: ButtonSize) {
  def classes: Mod[HtmlElement] =
    Seq(style.classes, size.classes)

  def small =
    copy(size = ButtonSize.Small)
}

object ButtonConfig {
  def delete  = ButtonConfig(ButtonStyle.Delete, ButtonSize.Normal)
  def gray    = ButtonConfig(ButtonStyle.Gray, ButtonSize.Normal)
  def normal  = ButtonConfig(ButtonStyle.Normal, ButtonSize.Normal)
  def success = ButtonConfig(ButtonStyle.Success, ButtonSize.Normal)
}

sealed trait ButtonSize extends Product with Serializable { self =>
  def classes: Mod[HtmlElement] =
    self match {
      case ButtonSize.Small  => cls("p-1 px-2 text-normal ml-2")
      case ButtonSize.Normal => cls("p-2 px-4 text-lg ml-6")
    }
}

object ButtonSize {
  case object Small  extends ButtonSize
  case object Normal extends ButtonSize
}

final case class Button(name: String, config: ButtonConfig, handleClick: () => Unit) extends Component {
  def body =
    button(
      config.classes,
      name,
      onClick --> { _ => handleClick() }
    )
}

final case class MiniButton(name: String, handleClick: () => Unit) extends Component {

  def body =
    button(
      div(
        cls("p-1 px-2 rounded bg-gray-200 text-gray-500 mb-4 hover:text-gray-400"),
        name
      ),
      onClick --> { _ =>
        handleClick()
      }
    )
}

object Components {

  def labeled(name: String, value: String): Div =
    div(
      cls("mr-8"),
      div(
        cls("text-sm text-gray-400 mb-1"),
        name
      ),
      div(value)
    )

  // format date like May 22, 1990
  def formatDate(localDate: LocalDate): String = {
    val month = localDate.getMonth.toString.toLowerCase.capitalize
    val day   = localDate.getDayOfMonth
    val year  = localDate.getYear
    s"$month $day, $year"
  }

}
