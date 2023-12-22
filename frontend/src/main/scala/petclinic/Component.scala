package petclinic

import com.raquo.laminar.api.L.HtmlElement
import com.raquo.laminar.modifiers.RenderableNode

trait Component {
  def body: HtmlElement
}

object Component {
  implicit def componentRenderableNode: RenderableNode[Component] =
    RenderableNode(
      _.body,
      _.map(_.body),
      _.map(_.body),
      _.map(_.body)
    )
}
