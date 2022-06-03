package petclinic

import com.raquo.laminar.api.L.{Owner => _, _}

trait Component {
  def body: HtmlElement
}

object Component {
  implicit def component2HtmlElement(component: Component): HtmlElement =
    component.body
}
