package petclinic

import com.raquo.laminar.api.L._

final case class Footer() extends Component {
  def body =
    div(
      cls(
        "sticky bottom-0 p-4 px-8 w-full bg-gray-200 whitespace-nowrap"
      ),
      div(
        cls(
          "flex justify-between"
        ),
        div(
          cls("text-gray-600 text-sm italic"),
          "⚠️ Data Will Reset Every 15 Minutes"
        ),
        div(
          cls("flex"),
          footerLink("ZIO Docs", "https://zio.dev/"),
          footerLink("GitHub Repo", "https://github.com/zio/zio-petclinic")
        )
      )
    )

  private def footerLink(name: String, url: String) =
    a(
      cls("text-gray-600 hover:text-gray-500 font-medium cursor-pointer ml-6"),
      target("_blank"),
      name,
      href(url)
    )
}
