package petclinic.models

import zio.json._

sealed trait Species {
  def name: String
}

object Species {

  case object Feline extends Species {
    override def name: String = "Feline"
  }

  case object Canine extends Species {
    override def name: String = "Canine"
  }

  case object Avia extends Species {
    override def name: String = "Avia"
  }

  case object Reptile extends Species {
    override def name: String = "Reptile"
  }

  case object Suidae extends Species {
    override def name: String = "Suidae"
  }

  def fromString(s: String): Species = s match {
    case "Feline"  => Feline
    case "Canine"  => Canine
    case "Avia"    => Avia
    case "Reptile" => Reptile
    case "Suidae"  => Suidae
  }

  val all: List[Species] = List(Feline, Canine, Avia, Reptile, Suidae)

  implicit val codec: JsonCodec[Species] = DeriveJsonCodec.gen[Species]

}
