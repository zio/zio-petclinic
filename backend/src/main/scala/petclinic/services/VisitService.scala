package petclinic.services

import zio.{Task, URLayer, ZEnvironment, ZIO, ZLayer}
import petclinic.QuillContext
import petclinic.models._

import javax.sql.DataSource

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

final case class VisitServiceLive(dataSource: DataSource, vetService: VetService) extends VisitService {

  import QuillContext._

  override def create(
      petId: PetId,
      date: java.time.LocalDate,
      description: String
  ): Task[Visit] =
    for {
      vets  <- vetService.getAll
      id     = vets(scala.util.Random.nextInt(vets.length)).id
      visit <- Visit.apply(petId, date, description, id)
      _     <- run(query[Visit].insertValue(lift(visit))).provideEnvironment(ZEnvironment(dataSource))
    } yield visit

  override def delete(id: VisitId): Task[Unit] =
    run(query[Visit].filter(_.id == lift(id)).delete)
      .provideEnvironment(ZEnvironment(dataSource))
      .unit

  override def get(id: VisitId): Task[Option[Visit]] =
    run(query[Visit].filter(_.id == lift(id)))
      .provideEnvironment(ZEnvironment(dataSource))
      .map(_.headOption)

  override def getForPet(petId: PetId): Task[List[Visit]] =
    run(query[Visit].filter(_.petId == lift(petId)).sortBy(_.date))
      .provideEnvironment(ZEnvironment(dataSource))

  override def getAll: Task[List[Visit]] =
    run(query[Visit])
      .provideEnvironment(ZEnvironment(dataSource))
      .map(_.toList)

  override def update(
      id: VisitId,
      date: Option[java.time.LocalDate],
      description: Option[String]
  ): Task[Unit] =
    run(
      dynamicQuery[Visit]
        .filter(_.id == lift(id))
        .update(setOpt(_.date, date), setOpt(_.description, description))
    )
      .provideEnvironment(ZEnvironment(dataSource))
      .unit

}

object VisitServiceLive {

  val layer: URLayer[DataSource with VetService, VisitService] =
    ZLayer.fromFunction(VisitServiceLive.apply _)

}
