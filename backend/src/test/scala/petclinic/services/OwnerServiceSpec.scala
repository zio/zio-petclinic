package petclinic.services

import zio.ZEnv
import zio.test._
import OwnerService._
import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer

// TODO: How should we deal with the accessors being used in the specs?

object OwnerServiceSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment, Throwable] = {
    suite("OwnerService")(
      suite("added owners exist in db")(
        test("returns true confirming existence of added owner") {
          for {
            owner <-
              create("Emily", "Elizabeth", "1 Birdwell Island, New York, NY", "212-215-1928", "emily@bigreddog.com")
            getOwner <- get(owner.id)
          } yield assertTrue(getOwner.get == owner)
        },
        test("returns true confirming existence of many added owners") {
          for {
            owner1 <- create("Fern", "Arable", "Arable Farm, Brooklin, ME", "207-711-1899", "fern@charlottesweb.com")
            owner2 <- create("Jon", "Arbuckle", "711 Maple St, Muncie, IN", "812-728-1945", "jon@garfield.com")
            owners <- getAll
          } yield assertTrue(owners.contains(owner1) && owners.contains(owner2))
        }
      ),
      suite("deleted owners do not exist in db")(
        test("returns false confirming non-existence of deleted owner") {
          for {
            owner <-
              create(
                "Sherlock",
                "Holmes",
                "221B Baker St, London, England, UK",
                "+44-20-7224-3688",
                "sherlock@sherlockholmes.com"
              )
            _     <- delete(owner.id)
            owner <- get(owner.id)
          } yield assertTrue(owner.isEmpty)
        },
        test("returns true confirming non-existence of many deleted owners") {
          for {
            owner1 <-
              create("Elizabeth", "Hunter", "Ontario, Canada", "807-511-1918", "elizabeth@incrediblejourney.com")
            owner2 <- create("Peter", "Hunter", "Ontario, Canada", "807-511-1918", "peter@incrediblejourney.com")
            owner3 <- create("Jim", "Hunter", "Ontario, Canada", "807-511-1918", "jim@incrediblejourney.com")
            _      <- delete(owner1.id)
            _      <- delete(owner2.id)
            owner1 <- get(owner1.id)
            owner2 <- get(owner2.id)
            owner3 <- get(owner3.id)
          } yield assertTrue(owner1.isEmpty && owner2.isEmpty && owner3.isDefined)
        }
      ),
      suite("updated owners contain accurate information")(
        test("returns true confirming updated owner information") {
          for {
            owner <- create(
                       "Harry",
                       "Potter",
                       "4 Privet Drive, Little Whinging, Surrey, UK",
                       "+44-20-7224-3688",
                       "harry@hogwarts.edu"
                     )
            _     <- update(owner.id, None, None, Some("12 Grimmauld Place, London, England, UK"), None, None)
            owner <- get(owner.id)
          } yield assertTrue(
            owner.get.firstName == "Harry" && owner.get.address == "12 Grimmauld Place, London, England, UK" && owner.get.address != "4 Privet Drive, Little Whinging, Surrey, UK"
          )
        }
      )
    ) @@ DbMigrationAspect.migrateOnce()() @@ TestAspect.withLiveRandom
  }
    .provideShared(
      OwnerServiceLive.layer,
      ZPostgreSQLContainer.Settings.default,
      ZPostgreSQLContainer.live,
      TestContainerLayers.dataSourceLayer,
      Live.default,
      ZEnv.live
    )
}
