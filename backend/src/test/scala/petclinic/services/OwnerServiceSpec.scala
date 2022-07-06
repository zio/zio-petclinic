package petclinic.services

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import zio._
import zio.test._

/** OwnerServiceSpec is a test suite for the OwnerService which allows us to
  * test that the methods we defined in the OwnerService work correctly. Because
  * our methods interact directly with the database and we want to limit
  * unnecessary database queries which could result in data inconsistencies in
  * the case of testing, we are using ZIO Test Containers to mimic a database.
  */
object OwnerServiceSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment, Throwable] = {
    suite("OwnerService")(
      suite("added owners exist in db")(
        test("returns true confirming existence of added owner") {
          for {
            owner <-
              OwnerService.create(
                "Emily",
                "Elizabeth",
                "1 Birdwell Island, New York, NY",
                "212-215-1928",
                "emily@bigreddog.com"
              )
            getOwner <- OwnerService.get(owner.id)
          } yield assertTrue(getOwner.get == owner)
        },
        test("returns true confirming existence of many added owners") {
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
            owners <- OwnerService.getAll
          } yield assertTrue(owners.contains(owner1) && owners.contains(owner2))
        }
      ),
      suite("deleted owners do not exist in db")(
        test("returns false confirming non-existence of deleted owner") {
          for {
            owner <-
              OwnerService.create(
                "Sherlock",
                "Holmes",
                "221B Baker St, London, England, UK",
                "+44-20-7224-3688",
                "sherlock@sherlockholmes.com"
              )
            _     <- OwnerService.delete(owner.id)
            owner <- OwnerService.get(owner.id)
          } yield assertTrue(owner.isEmpty)
        },
        test("returns true confirming non-existence of many deleted owners") {
          for {
            owner1 <-
              OwnerService.create(
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
            _      <- OwnerService.delete(owner1.id)
            _      <- OwnerService.delete(owner2.id)
            owner1 <- OwnerService.get(owner1.id)
            owner2 <- OwnerService.get(owner2.id)
            owner3 <- OwnerService.get(owner3.id)
          } yield assertTrue(owner1.isEmpty && owner2.isEmpty && owner3.isDefined)
        }
      ),
      suite("updated owners contain accurate information")(
        test("returns true confirming updated owner information") {
          for {
            owner <- OwnerService.create(
                       "Harry",
                       "Potter",
                       "4 Privet Drive, Little Whinging, Surrey, UK",
                       "+44-20-7224-3688",
                       "harry@hogwarts.edu"
                     )
            _     <- OwnerService.update(owner.id, None, None, Some("12 Grimmauld Place, London, England, UK"), None, None)
            owner <- OwnerService.get(owner.id)
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
