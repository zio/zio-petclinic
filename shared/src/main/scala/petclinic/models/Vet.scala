package petclinic.models

import zio.json._

final case class Vet(id: VetId, lastName: String, specialty: String)

object Vet {
  implicit val codec: JsonCodec[Vet] = DeriveJsonCodec.gen[Vet]
}
