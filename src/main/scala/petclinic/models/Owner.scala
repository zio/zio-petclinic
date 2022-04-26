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

// represents an owner of a pet
final case class Owner(id: OwnerId, firstName: String, lastName: String, phone: String)

object Owner {
  def apply(firstName: String, lastName: String, phone: String): ZIO[Random, Nothing, Owner] =
    OwnerId.random.map(id => Owner(id, firstName, lastName, phone))
}
