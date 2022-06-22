package petclinic.services

import zio._
import petclinic.QuillContext
import petclinic.models.{PetId, Visit, VisitId}

import javax.sql.DataSource

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
      visit <- Visit.make(petId, date, description, id)
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
