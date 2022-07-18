package petclinic.models

import zio._
import zio.json._

/** Owner defines what pieces of data an Owner is comprised of.
  *
  * This data type models what we expect to be defined in the database.
  */
final case class Owner(
    id: OwnerId,
    firstName: String,
    lastName: String,
    address: String,
    phone: String,
    email: String
) {
  def fullName: String = firstName + " " + lastName
}

object Owner {

  /** Uses the `random` method defined on our OwnerId wrapper to generate a
    * random ID and assign that to the Owner we are creating.
    */
  def make(firstName: String, lastName: String, address: String, phone: String, email: String): UIO[Owner] =
    OwnerId.random.map(Owner(_, firstName, lastName, address, phone, email))

  /** Derives a JSON codec for the Owner type allowing it to be (de)serialized.
    */
  implicit val codec: JsonCodec[Owner] =
    DeriveJsonCodec.gen[Owner]

}
