package petclinic.services

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import petclinic.models._
import zio._
import zio.test._

import java.time.LocalDate

/** A test suite for PetService which allows us to test that the methods defined
  * in it work as expected.
  *
  * Because the methods interact directly with the database and we want to limit
  * unnecessary queries to our local database, which could result in data
  * inconsistencies, we are using ZIO Test Containers to create a temporary
  * database.
  */
object PetServiceSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment, Throwable] = {
    suite("PetService")(
      suite("added pets exist in db")(
        test("returns true confirming existence of added pet") {
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
            getPet <- PetService.get(pet.id)
          } yield assertTrue(getPet.get == pet)
        },
        test("returns true confirming existence of many added pets") {
          for {
            owner1 <- OwnerService.create(
                        "Fern",
                        "Arable",
                        "Arable Farm, Brooklin, ME",
                        "207-711-1899",
                        "fern@charlottesweb.com"
                      )
            owner2 <-
              OwnerService.create("Jon", "Arbuckle", "711 Maple St, Muncie, IN", "812-728-1945", "jon@garfield.com")
            pet1 <-
              PetService.create("Wilbur", LocalDate.of(1952, 10, 15), Species.Suidae, owner1.id)
            pet2 <-
              PetService.create("Garfield", LocalDate.of(1978, 6, 19), Species.Feline, owner2.id)
            pets <- PetService.getAll
          } yield assertTrue(pets.contains(pet1) && pets.contains(pet2))
        }
      ),
      suite("deleted pets do not exist in db")(
        test("returns true confirming non-existence of deleted pet") {
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
            _      <- PetService.delete(pet.id)
            getPet <- PetService.get(pet.id)
          } yield assertTrue(getPet.isEmpty)
        },
        test("returns true confirming the non-existence of many deleted pets") {
          for {
            owner1 <- OwnerService.create(
                        "Elizabeth",
                        "Hunter",
                        "Ontario, Canada",
                        "807-511-1918",
                        "elizabeth@incrediblejourney.com"
                      )
            owner2 <-
              OwnerService.create("Peter", "Hunter", "Ontario, Canada", "807-511-1918", "peter@incrediblejourney.com")
            owner3 <-
              OwnerService.create("Jim", "Hunter", "Ontario, Canada", "807-511-1918", "jim@incrediblejourney.com")
            pet1 <-
              PetService.create("Tao", LocalDate.of(1963, 11, 20), Species.Feline, owner1.id)
            pet2 <-
              PetService.create("Bodger", LocalDate.of(1963, 11, 20), Species.Canine, owner2.id)
            pet3 <-
              PetService.create("Luath", LocalDate.of(1963, 11, 20), Species.Canine, owner3.id)
            _       <- PetService.delete(pet1.id)
            _       <- PetService.delete(pet2.id)
            getPet1 <- PetService.get(pet1.id)
            getPet2 <- PetService.get(pet2.id)
            getPet3 <- PetService.get(pet3.id)
          } yield assertTrue(getPet1.isEmpty && getPet2.isEmpty && getPet3.isDefined)
        }
      ),
      suite("updated pets contain accurate information")(
        test("returns true confirming updated pet information") {
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
            _      <- PetService.update(pet.id, Some("Hedwig"), None, None, None)
            getPet <- PetService.get(pet.id)
          } yield assertTrue(getPet.get.name == "Hedwig")
        }
      )
    ) @@ DbMigrationAspect.migrateOnce()() @@ TestAspect.withLiveRandom
  }.provideShared(
    PetServiceLive.layer,
    OwnerServiceLive.layer,
    ZPostgreSQLContainer.Settings.default,
    ZPostgreSQLContainer.live
  )

}
