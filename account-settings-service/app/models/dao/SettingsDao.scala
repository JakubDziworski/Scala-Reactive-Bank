package models.dao

import models.domain.Setting

import scala.concurrent.Future

/**
  * Created by kuba on 11.09.16.
  */
trait SettingsDao {

  def saveSettings(setting: Setting): Future[Unit]

  def getCurrentSettings(accountId: Long): Future[Setting]

}
