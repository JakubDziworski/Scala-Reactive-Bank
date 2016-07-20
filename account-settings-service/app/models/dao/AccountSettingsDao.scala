package models.dao


import javax.inject.Singleton

import com.typesafe.scalalogging.LazyLogging
import exceptions.SettingForIdDoesNotExistException
import models.{PermissionCheck, Settings}
import play.api.mvc.Result

import scala.collection.mutable

/**
  * Created by kuba on 26.05.16.
  */
@Singleton
class AccountSettingsDao extends LazyLogging {

  val settings: mutable.Map[Long, Settings] = mutable.HashMap[Long, Settings](1L -> Settings(1, Some(3000)))

  def saveSettings(mergedSettings: Settings): Unit = {
    settings += (mergedSettings.accountId -> mergedSettings)
  }

  def getCurrentSettings(accountId: Long): Option[Settings] = {
      settings.get(accountId)
  }

  def isAllowed(permissionCheck: PermissionCheck): Boolean = {
    return settings.get(permissionCheck.accountId).map( s => {
      permissionCheck.cashAmount <= s.transactionValueLimit.getOrElse(50000)
    }).getOrElse(false)
  }

}


