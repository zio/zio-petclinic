package petclinic.views.components

import com.raquo.laminar.api.L._
import petclinic.Component
import ButtonConfig.{ButtonStyle, ButtonSize}

final case class Button(name: String, config: ButtonConfig, handleClick: () => Unit, isSubmit: Boolean = false)
    extends Component {
  def body: HtmlElement =
    button(
      config.classes,
      name,
      onClick --> { _ => handleClick() },
      `type`(if (isSubmit) "submit" else "button")
    )
}

final case class ButtonConfig(style: ButtonStyle, size: ButtonSize) {
  def classes: Mod[HtmlElement] =
    Seq(style.classes, size.classes)

  def small: ButtonConfig =
    copy(size = ButtonSize.Small)
}

object ButtonConfig {
  val gray: ButtonConfig    = ButtonConfig(ButtonStyle.Gray, ButtonSize.Small)
  val delete: ButtonConfig  = ButtonConfig(ButtonStyle.Delete, ButtonSize.Medium)
  val normal: ButtonConfig  = ButtonConfig(ButtonStyle.Normal, ButtonSize.Medium)
  val success: ButtonConfig = ButtonConfig(ButtonStyle.Success, ButtonSize.Medium)

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

  sealed trait ButtonSize extends Product with Serializable { self =>
    def classes: Mod[HtmlElement] =
      self match {
        case ButtonSize.Small  => cls("p-1 px-2 text-normal")
        case ButtonSize.Medium => cls("p-2 px-4 text-lg")
      }
  }

  object ButtonSize {
    case object Small  extends ButtonSize
    case object Medium extends ButtonSize
  }
}
