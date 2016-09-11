package models.dao

import com.typesafe.scalalogging.LazyLogging
import models.domain.Setting
import slick.driver.PostgresDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by kuba on 26.05.16.
  */
class SettingsPostgresDao extends SettingsDao {

  val settings = TableQuery[SettingsTable]

  val db = Database.forConfig("mydb")
  Await.result(db.run(MTable.getTables).flatMap {
    case tables if !tables.exists(_.name.name == settings.baseTableRow.tableName) => {
      db.run(settings.schema.create)
    }
    case _ => Future.successful("schema already created")
  }, Duration.Inf)

  def saveSettings(setting: Setting): Future[Unit] = {
    db.run(settings.insertOrUpdate(setting)).map(_ => Future.successful())
  }

  def getCurrentSettings(accountId: Long): Future[Setting] = {
    db.run(settings.filter(_.accountId === accountId).result.head)
  }
}


