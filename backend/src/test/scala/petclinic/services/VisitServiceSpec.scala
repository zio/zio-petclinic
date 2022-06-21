package petclinic.services

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import petclinic.models._
import zio.ZEnv
import zio.test._

import java.time.LocalDate

object VisitServiceSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment, Throwable] = {
    suite("VisitService")(
      suite("added visits exist in db")(
        test("returns true confirming existence of added visit") {
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
            visit <- VisitService.create(
                       pet.id,
                       LocalDate.of(2022, 6, 12),
                       "Get updated body measurements"
                     )
            getVisit <- VisitService.get(visit.id)
          } yield assertTrue(getVisit.get == visit)
        },
        test("returns true confirming existence of many added visits") {
          for {
            owner <-
              OwnerService.create("Jon", "Arbuckle", "711 Maple St, Muncie, IN", "812-728-1945", "jon@garfield.com")
            pet <-
              PetService.create("Garfield", LocalDate.of(1978, 6, 19), Species.Feline, owner.id)
            visit1 <-
              VisitService.create(pet.id, LocalDate.of(2022, 7, 1), "Lasagna allergy test")
            visit2 <-
              VisitService.create(pet.id, LocalDate.of(2022, 7, 11), "Monday allergy test")
            visits <- VisitService.getAll
          } yield assertTrue(visits.contains(visit1) && visits.contains(visit2))
        }
      ),
      suite("deleted visits do not exist in db")(
        test("returns true confirming non-existence of deleted visit") {
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
            visit <-
              VisitService.create(
                pet.id,
                LocalDate.of(2022, 8, 23),
                "Have scent detection measured"
              )
            _        <- VisitService.delete(visit.id)
            getVisit <- VisitService.get(visit.id)
          } yield assertTrue(getVisit.isEmpty)
        },
        test("returns true confirming the non-existence of many deleted visits") {
          for {
            owner <-
              OwnerService.create("Peter", "Hunter", "Ontario, Canada", "807-511-1918", "peter@incrediblejourney.com")
            pet <-
              PetService.create("Bodger", LocalDate.of(1963, 11, 20), Species.Canine, owner.id)
            visit1 <-
              VisitService.create(pet.id, LocalDate.of(2022, 5, 22), "Immunization")
            visit2 <-
              VisitService.create(pet.id, LocalDate.of(2022, 5, 23), "Immunization")
            visit3 <-
              VisitService.create(pet.id, LocalDate.of(2022, 5, 24), "Immunization")
            _         <- VisitService.delete(visit1.id)
            _         <- VisitService.delete(visit2.id)
            getVisit1 <- VisitService.get(visit1.id)
            getVisit2 <- VisitService.get(visit2.id)
            getVisit3 <- VisitService.get(visit3.id)
          } yield assertTrue(getVisit1.isEmpty && getVisit2.isEmpty && getVisit3.isDefined)
        }
      ),
      suite("updated visits contain accurate information")(
        test("returns true confirming updated visit information") {
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
            visit <-
              VisitService.create(
                pet.id,
                LocalDate.of(2022, 7, 27),
                "Broken wing"
              )
            _        <- VisitService.update(visit.id, None, Some("Two broken wings"))
            getVisit <- VisitService.get(visit.id)
          } yield assertTrue(getVisit.get.description == "Two broken wings")
        }
      )
    ) @@ DbMigrationAspect.migrateOnce()() @@ TestAspect.withLiveRandom
  }.provideShared(
    PetServiceLive.layer,
    OwnerServiceLive.layer,
    ZPostgreSQLContainer.Settings.default,
    ZPostgreSQLContainer.live,
    VisitServiceLive.layer,
    VetServiceLive.layer,
    TestContainerLayers.dataSourceLayer,
    Live.default,
    ZEnv.live
  )

}
