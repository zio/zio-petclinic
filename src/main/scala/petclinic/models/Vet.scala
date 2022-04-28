package petclinic.models

import java.util.UUID

final case class VetId(id: UUID) extends AnyVal

final case class Vet(id: VetId, firstName: String, lastName: String, specialty: String)
