package petclinic.models

import zio.UIO
import zio.json._

/** Visit defines what pieces of data a Visit is comprised of.
  *
  * This data type models what we expect to be defined in the database.
  */
final case class Visit(
    id: VisitId,
    petId: PetId,
    date: java.time.LocalDate,
    description: String,
    vetId: VetId
)

object Visit {

  /** Uses the `random` method defined on our VisitId wrapper to generate a
    * random ID and assign that to the Visit we are creating.
    */
  def make(
      petId: PetId,
      date: java.time.LocalDate,
      description: String,
      vetId: VetId
  ): UIO[Visit] =
    VisitId.random.map(id => Visit(id, petId, date, description, vetId))

  /** Derives a JSON codec for the Visit type allowing it to be (de)serialized.
    */
  implicit val codec: JsonCodec[Visit] = DeriveJsonCodec.gen[Visit]

}
