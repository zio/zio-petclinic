package petclinic.models

import zio.json.JsonCodec

import java.util.UUID

final case class VetId(id: UUID) extends AnyVal

object VetId {

  implicit val codec: JsonCodec[VetId] = JsonCodec[UUID].transform(VetId(_), _.id)
}

final case class Vet(id: VetId, lastName: String, specialty: String)
