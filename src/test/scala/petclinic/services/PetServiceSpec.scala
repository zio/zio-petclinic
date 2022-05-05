package petclinic.services

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import petclinic.models.Species
import zio.test._

import java.time.LocalDate

object PetServiceSpec extends DefaultRunnableSpec {

  override def spec: Spec[TestEnvironment, TestFailure[Throwable], TestSuccess] = {
    suite("PetService")(
      suite("added pets exist in db")(
        test("returns true confirming existence of added pet") {
          for {
            createOwner <- OwnerService.create("Emily", "Elizabeth", "1 Birdwell Island, New York, NY", "212-215-1928")
            createPet <-
              PetService.create("Clifford", LocalDate.of(1962, 2, 14), Species.fromString("Canine"), createOwner.id)
            pet <- PetService.get(createPet.id)
          } yield assertTrue(pet.get == createPet)
        },
        test("returns true confirming existence of many added pets") {
          for {
            createOwner1 <- OwnerService.create("Fern", "Arable", "Arable Farm, Brooklin, ME", "207-711-1899")
            createOwner2 <- OwnerService.create("Jon", "Arbuckle", "711 Maple St, Muncie, IN", "812-728-1945")
            createPet1 <-
              PetService.create("Wilbur", LocalDate.of(1952, 10, 15), Species.fromString("Suidae"), createOwner1.id)
            createPet2 <-
              PetService.create("Garfield", LocalDate.of(1978, 6, 19), Species.fromString("Feline"), createOwner2.id)
            pets <- PetService.getAll
          } yield assertTrue(pets.contains(createPet1) && pets.contains(createPet2))
        }
      ),
      suite("deleted pets do not exist in db")(
        test("returns true confirming non-existence of deleted pet") {
          for {
            createOwner <-
              OwnerService.create("Sherlock", "Holmes", "221B Baker St, London, England, UK", "+44-20-7224-3688")
            createPet <-
              PetService.create("Toby", LocalDate.of(1888, 4, 17), Species.fromString("Canine"), createOwner.id)
            _   <- PetService.delete(createPet.id)
            pet <- PetService.get(createPet.id)
          } yield assertTrue(pet.isEmpty)
        },
        test("returns true confirming the non-existence of many deleted pets") {
          for {
            createOwner1 <- OwnerService.create("Elizabeth", "Hunter", "Ontario, Canada", "807-511-1918")
            createOwner2 <- OwnerService.create("Peter", "Hunter", "Ontario, Canada", "807-511-1918")
            createOwner3 <- OwnerService.create("Jim", "Hunter", "Ontario, Canada", "807-511-1918")
            createPet1 <-
              PetService.create("Tao", LocalDate.of(1963, 11, 20), Species.fromString("Feline"), createOwner1.id)
            createPet2 <-
              PetService.create("Bodger", LocalDate.of(1963, 11, 20), Species.fromString("Canine"), createOwner2.id)
            createPet3 <-
              PetService.create("Luath", LocalDate.of(1963, 11, 20), Species.fromString("Canine"), createOwner3.id)
            _    <- PetService.delete(createPet1.id)
            _    <- PetService.delete(createPet2.id)
            pet1 <- PetService.get(createPet1.id)
            pet2 <- PetService.get(createPet2.id)
            pet3 <- PetService.get(createPet3.id)
          } yield assertTrue(pet1.isEmpty && pet2.isEmpty && pet3.isDefined)
        }
      ),
      suite("updated pets contain accurate information")(
        test("returns true confirming updated pet information") {
          for {
            createOwner <-
              OwnerService.create("Harry", "Potter", "4 Privet Drive, Little Whinging, Surrey, UK", "+44-20-7224-3688")
            createPet <-
              PetService.create("Snowy Owl", LocalDate.of(1991, 1, 1), Species.fromString("Avia"), createOwner.id)
            _   <- PetService.update(createPet.id, Some("Hedwig"), None, None, None)
            pet <- PetService.get(createPet.id)
          } yield assertTrue(pet.get.name == "Hedwig")
        }
      )
    ) @@ DbMigrationAspect.migrate()()
  }.provideCustomShared(
    PetServiceLive.layer,
    OwnerServiceLive.layer,
    ZPostgreSQLContainer.Settings.default,
    ZPostgreSQLContainer.live,
    TestContainerLayers.dataSourceLayer
  )

}
