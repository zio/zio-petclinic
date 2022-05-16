package petclinic

import com.raquo.laminar.api.L._
import org.scalajs.dom
import scalajs.js
import scala.scalajs.js.annotation.JSImport

object Frontend {

  def main(args: Array[String]): Unit = {
    val appNode = dom.document.querySelector("#app")
    render(appNode, HomePage.body)
  }
}
