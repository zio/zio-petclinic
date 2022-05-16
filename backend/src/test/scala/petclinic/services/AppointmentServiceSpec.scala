package petclinic.services

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import zio.test._
import petclinic.models._

import java.time.{LocalDate, LocalDateTime}

object AppointmentServiceSpec extends ZIOSpecDefault {

  override def spec: ZSpec[TestEnvironment, Throwable] = {
    suite("AppointmentService")(
      suite("added appointments exist in db")(
        test("returns true confirming existence of added appointment") {
          for {
            owner <- OwnerService.create(
                       "Emily",
                       "Elizabeth",
                       "1 Birdwell Island, New York, NY",
                       "212-215-1928",
                       "emily@bigreddog.com"
                     )
            pet <-
              PetService.create("Clifford", LocalDate.of(1962, 2, 14), Species.Canine, owner.id)
            appointment <- AppointmentService.create(
                             pet.id,
                             LocalDateTime.of(2022, 6, 12, 13, 0),
                             "Get updated body measurements"
                           )
            getAppointment <- AppointmentService.get(appointment.id)
          } yield assertTrue(getAppointment.get == appointment)
        },
        test("returns true confirming existence of many added appointments") {
          for {
            owner <-
              OwnerService.create("Jon", "Arbuckle", "711 Maple St, Muncie, IN", "812-728-1945", "jon@garfield.com")
            pet <-
              PetService.create("Garfield", LocalDate.of(1978, 6, 19), Species.Feline, owner.id)
            appointment1 <-
              AppointmentService.create(pet.id, LocalDateTime.of(2022, 7, 1, 9, 0), "Lasagna allergy test")
            appointment2 <-
              AppointmentService.create(pet.id, LocalDateTime.of(2022, 7, 11, 10, 0), "Monday allergy test")
            appointments <- AppointmentService.getAll
          } yield assertTrue(appointments.contains(appointment1) && appointments.contains(appointment2))
        }
      ),
      suite("deleted appointments do not exist in db")(
        test("returns true confirming non-existence of deleted appointment") {
          for {
            owner <-
              OwnerService.create(
                "Sherlock",
                "Holmes",
                "221B Baker St, London, England, UK",
                "+44-20-7224-3688",
                "sherlock@sherlockholmes.com"
              )
            pet <-
              PetService.create("Toby", LocalDate.of(1888, 4, 17), Species.Canine, owner.id)
            appointment <-
              AppointmentService.create(
                pet.id,
                LocalDateTime.of(2022, 8, 23, 11, 0),
                "Have scent detection measured"
              )
            _              <- AppointmentService.delete(appointment.id)
            getAppointment <- AppointmentService.get(appointment.id)
          } yield assertTrue(getAppointment.isEmpty)
        },
        test("returns true confirming the non-existence of many deleted appointments") {
          for {
            owner <-
              OwnerService.create("Peter", "Hunter", "Ontario, Canada", "807-511-1918", "peter@incrediblejourney.com")
            pet <-
              PetService.create("Bodger", LocalDate.of(1963, 11, 20), Species.Canine, owner.id)
            appointment1 <-
              AppointmentService.create(pet.id, LocalDateTime.of(2022, 5, 22, 9, 0), "Immunization")
            appointment2 <-
              AppointmentService.create(pet.id, LocalDateTime.of(2022, 5, 23, 9, 0), "Immunization")
            appointment3 <-
              AppointmentService.create(pet.id, LocalDateTime.of(2022, 5, 24, 9, 0), "Immunization")
            _               <- AppointmentService.delete(appointment1.id)
            _               <- AppointmentService.delete(appointment2.id)
            getAppointment1 <- AppointmentService.get(appointment1.id)
            getAppointment2 <- AppointmentService.get(appointment2.id)
            getAppointment3 <- AppointmentService.get(appointment3.id)
          } yield assertTrue(getAppointment1.isEmpty && getAppointment2.isEmpty && getAppointment3.isDefined)
        }
      ),
      suite("updated appointments contain accurate information")(
        test("returns true confirming updated appointment information") {
          for {
            owner <-
              OwnerService.create(
                "Harry",
                "Potter",
                "4 Privet Drive, Little Whinging, Surrey, UK",
                "+44-20-7224-3688",
                "harry@hogwarts.edu"
              )
            pet <-
              PetService.create("Snowy Owl", LocalDate.of(1991, 1, 1), Species.Avia, owner.id)
            appointment <-
              AppointmentService.create(
                pet.id,
                LocalDateTime.of(2022, 7, 27, 14, 0),
                "Broken wing"
              )
            _              <- AppointmentService.update(appointment.id, None, Some("Two broken wings"))
            getAppointment <- AppointmentService.get(appointment.id)
          } yield assertTrue(getAppointment.get.description == "Two broken wings")
        }
      )
    ) @@ DbMigrationAspect.migrate()()
  }.provideShared(
    PetServiceLive.layer,
    OwnerServiceLive.layer,
    ZPostgreSQLContainer.Settings.default,
    ZPostgreSQLContainer.live,
    AppointmentServiceLive.layer,
    VetServiceLive.layer,
    TestContainerLayers.dataSourceLayer
  )

}
