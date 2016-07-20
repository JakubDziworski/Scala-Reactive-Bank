package models.dao
import javax.inject.Singleton

import com.typesafe.scalalogging.LazyLogging
import models.tables.SettingsTable
import models.{PermissionCheck, Setting}
import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
/**
  * Created by kuba on 26.05.16.
  */
@Singleton
class AccountSettingsDao extends LazyLogging {

  val settings = TableQuery[SettingsTable]

  val db = Database.forConfig("mydb")
  Await.result(db.run(MTable.getTables).flatMap {
    case tables if !tables.exists(_.name.name == settings.baseTableRow.tableName)=> {
      print("creating schema")
      db.run(settings.schema.create)
    }
    case _ => {
      print("schema already created")
      Future.successful("schema already created")
    }
  }, Duration.Inf)


  def saveSettings(setting: Setting): Future[Unit] = {
    db.run(
      settings += setting
    ).map(_ => Future.successful())
  }

  def getCurrentSettings(accountId: Long): Future[Setting] = {
    db.run(
      settings.filter(_.accountId === accountId).result.head
    )
  }

  def isAllowed(permissionCheck: PermissionCheck): Future[Boolean] = {
    return getCurrentSettings(permissionCheck.accountId).map( s => {
      permissionCheck.cashAmount < s.transactionValueLimit.getOrElse(50000)
    })
  }

}


