package petclinic.views.components

import com.raquo.laminar.api.L._
import petclinic.Style

import java.time.LocalDate

object Components {

  def labeled(name: String, value: String): Div =
    div(
      cls("mr-8"),
      div(
        Style.header,
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
