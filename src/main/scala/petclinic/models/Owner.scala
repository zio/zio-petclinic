package petclinic.models

import zio._
import java.util.UUID



// wraps UUID as a specific OwnerId so that we cannot use an incorrect UUID
final case class OwnerId(id: UUID) extends AnyVal

object OwnerId {
  def random: ZIO[Random, Nothing, OwnerId] = Random.nextUUID.map(OwnerId(_))
}


// extracts address to reduce the number of fields in owner
final case class Address(ownerId: OwnerId, street: String, city: String, state: String, zip: String)

object Address {
  def make(ownerId: OwnerId, street: String, city: String, state: String, zip: String): Address =
    Address(ownerId, street, city, state, zip)
}


// represents an owner of a pet
final case class Owner(id: OwnerId, firstName: String, lastName: String, address: Address, ph: String)

object Owner {
  def make(firstName: String, lastName: String, address: Address, ph: String): ZIO[Random, Nothing, Owner] = {
    OwnerId.random.map(id => Owner(id , firstName, lastName, address, ph))
  }
}




