package petclinic.services

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import zio.test._

object OwnerServiceSpec extends DefaultRunnableSpec {

  import OwnerService._

  override def spec: Spec[TestEnvironment, TestFailure[Throwable], TestSuccess] = {
    suite("OwnerService")(
      suite("added owners exist in db")(
        test("returns true confirming existence of added owner") {
          for {
            createOwner <- create("Emily", "Elizabeth", "1 Birdwell Island, New York, NY", "212-215-1928")
            owner       <- get(createOwner.id)
          } yield assertTrue(owner.get == createOwner)
        },
        test("returns true confirming existence of many added owners") {
          for {
            createOwner1 <- create("Fern", "Arable", "Arable Farm, Brooklin, ME", "207-711-1899")
            createOwner2 <- create("Jon", "Arbuckle", "711 Maple St, Muncie, IN", "812-728-1945")
            owners       <- getAll
          } yield assertTrue(owners.contains(createOwner1) && owners.contains(createOwner2))
        }
      ),
      suite("deleted owners do not exist in db")(
        test("returns false confirming non-existence of deleted owner") {
          for {
            createOwner <-
              create("Sherlock", "Holmes", "221B Baker St, London, England, UK", "+44-20-7224-3688")
            _     <- delete(createOwner.id)
            owner <- get(createOwner.id)
          } yield assertTrue(owner.isEmpty)
        },
        test("returns true confirming non-existence of many deleted owners") {
          for {
            createOwner1 <- create("Elizabeth", "Hunter", "Ontario, Canada", "807-511-1918")
            createOwner2 <- create("Peter", "Hunter", "Ontario, Canada", "807-511-1918")
            createOwner3 <- create("Jim", "Hunter", "Ontario, Canada", "807-511-1918")
            _            <- delete(createOwner1.id)
            _            <- delete(createOwner2.id)
            owner1       <- get(createOwner1.id)
            owner2       <- get(createOwner2.id)
            owner3       <- get(createOwner3.id)
          } yield assertTrue(owner1.isEmpty && owner2.isEmpty && owner3.isDefined)
        }
      ),
      suite("updated owners contain accurate information")(
        test("returns true confirming updated owner information") {
          for {
            createOwner <- create("Harry", "Potter", "4 Privet Drive, Little Whinging, Surrey, UK", "+44-20-7224-3688")
            _           <- update(createOwner.id, None, None, Some("12 Grimmauld Place, London, England, UK"), None)
            owner       <- get(createOwner.id)
          } yield assertTrue(
            owner.get.firstName == "Harry" && owner.get.address == "12 Grimmauld Place, London, England, UK" && owner.get.address != "4 Privet Drive, Little Whinging, Surrey, UK"
          )
        }
      )
    ) @@ DbMigrationAspect.migrate()()
  }
    .provideCustomShared(
      OwnerServiceLive.layer,
      ZPostgreSQLContainer.Settings.default,
      ZPostgreSQLContainer.live,
      TestContainerLayers.dataSourceLayer
    )
}
