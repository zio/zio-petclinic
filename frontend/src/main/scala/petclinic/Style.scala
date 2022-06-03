package petclinic

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L._

object Style {

  type Mod = Modifier[HtmlElement]

  def padding: Mod =
    L.padding("12px")

  def red: Mod =
    L.background("red")

  def serifFont =
    fontFamily("Libre Baskerville")

  def fontSize16 =
    fontSize("16px")

  def fontSize24 =
    fontSize("24px")

  def fontSize32 =
    fontSize("32px")

}
