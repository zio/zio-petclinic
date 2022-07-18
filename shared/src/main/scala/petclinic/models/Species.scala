package petclinic.models

import zio.json._

/** Species is a wrapper for a string which allows us to further explicitly
  * define what types of Species exist in our application.
  */
sealed trait Species {
  def name: String
}

/** The companion object houses the definitive types of Species and wraps their
  * string representation in a case object.
  *
  * This conveniently allows Species to be matched on.
  */
object Species {

  case object Empty extends Species {
    override def name: String = "Select..."
  }

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

  /** Converts a string to its Species representation. */
  def fromString(s: String): Species = s match {
    case "Feline"  => Feline
    case "Canine"  => Canine
    case "Avia"    => Avia
    case "Reptile" => Reptile
    case "Suidae"  => Suidae
    case _         => Empty
  }

  /** Compiles all of our Species subtypes into a List. */
  val all: List[Species] = List(Empty, Feline, Canine, Avia, Reptile, Suidae)

  /** Derives a JSON codec for Species allowing it to be de(serialized). */
  implicit val codec: JsonCodec[Species] = DeriveJsonCodec.gen[Species]

}
