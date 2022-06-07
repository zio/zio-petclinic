package petclinic

import com.raquo.laminar.api.L._

object Style {

  type Mod = Modifier[HtmlElement]

  def serifFont: Mod =
    fontFamily("Libre Baskerville")

}
