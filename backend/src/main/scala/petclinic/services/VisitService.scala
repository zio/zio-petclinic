package petclinic.services

import zio._
import petclinic.models._

trait VisitService {

  def create(petId: PetId, date: java.time.LocalDate, description: String): Task[Visit]

  def delete(id: VisitId): Task[Unit]

  def get(id: VisitId): Task[Option[Visit]]

  def getAll: Task[List[Visit]]

  def getForPet(petId: PetId): Task[List[Visit]]

  def update(id: VisitId, date: Option[java.time.LocalDate], description: Option[String]): Task[Unit]

}

object VisitService {

  def create(
      petId: PetId,
      date: java.time.LocalDate,
      description: String
  ): ZIO[VisitService, Throwable, Visit] =
    ZIO.serviceWithZIO[VisitService](_.create(petId, date, description))

  def delete(id: VisitId): ZIO[VisitService, Throwable, Unit] =
    ZIO.serviceWithZIO[VisitService](_.delete(id))

  def get(id: VisitId): ZIO[VisitService, Throwable, Option[Visit]] =
    ZIO.serviceWithZIO[VisitService](_.get(id))

  def getForPet(petId: PetId): ZIO[VisitService, Throwable, List[Visit]] =
    ZIO.serviceWithZIO[VisitService](_.getForPet(petId))

  def getAll: ZIO[VisitService, Throwable, List[Visit]] = ZIO.serviceWithZIO[VisitService](_.getAll)

  def update(
      id: VisitId,
      date: Option[java.time.LocalDate],
      description: Option[String]
  ): ZIO[VisitService, Throwable, Unit] =
    ZIO.serviceWithZIO[VisitService](_.update(id, date, description))

}
