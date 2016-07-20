package models.dao

import akka.actor.Status.Success
import org.specs2._
import org.specs2.specification.Scope
import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by kuba on 27.06.16.
  */
class TransactionDaoTest extends mutable.Specification {

  "The schma should be created " in new initializedSchema {
    val future = db.run(
      transactions.schema.create
    ).flatMap {
      case _ => db.run(MTable.getTables)
    }

    val tables = Await.result(future,Duration.Inf)
    assert(tables.size == 1)
    assert(tables(0).name.name == "TRANSACTIONS")
  }
}

trait initializedSchema extends Scope {
  val db: Database = Database.forConfig("h2mem1")
  val transactions = TableQuery[Transactions]
}
