package petclinic

import com.raquo.laminar.api.L._

object Style {

  type Mod = Modifier[HtmlElement]

  def serifFont: Mod =
    fontFamily("Libre Baskerville")

  def header: Mod =
    cls("text-sm text-gray-400 mb-1")

  def boldHeader: Mod =
    cls("text-4xl text-orange-600 mb-4")

  def bodyText: Mod = ???

}
