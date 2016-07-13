package controllers

import javax.inject.Inject

import com.typesafe.scalalogging.LazyLogging
import io.swagger.annotations.{ApiImplicitParams, _}
import models.{PermissionCheck, Settings}
import models.dao.AccountSettingsDao
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{Action, Controller}

/**
  * Created by kuba on 25.05.16.
  */

@Api("Account Settings Service")
case class AccountSettingsController @Inject()(accountSettingsDao: AccountSettingsDao) extends Controller with LazyLogging {

  @ApiOperation(value = "Test if API is reachable")
  def test = Action {
    Ok("Reactive Bank - Account Settings Service - REACHABLE")
  }

  //set max transaction value
  //get max transaction value
  //deactivate credit card
  //activate credit card

  @ApiOperation(value = "set settings")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "settings", dataType = "models.Settings", required = true, paramType = "body")))
  def setSettings = Action(parse.json) { request =>
    Json.fromJson[Settings](request.body) match {
      case JsSuccess(newSettings, _) => {
        val currentSettings = accountSettingsDao.getCurrentSettings(newSettings.accountId)
        val mergedSettings = if (currentSettings.isDefined) Settings.merge(currentSettings.get, newSettings) else newSettings;
        accountSettingsDao.saveSettings(mergedSettings)
        Ok("Sucesfully changed settings")
      }
    }
  }

//  @ApiOperation(value = "get settings")
//  def getSettings(accountId:Long) = Action { request =>
//    Ok(Json.toJson[Settings](accountSettingsDao.getCurrentSettings(accountId)))
//  }


  implicit def tuplesWrites = Json.reads[PermissionCheck]

  @ApiOperation(value = "checkPermissions")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "checkPermissions", dataType = "models.PermissionCheck", required = true, paramType = "body")))
  def checkPermissions = Action(parse.json) { request =>
    Json.fromJson[PermissionCheck](request.body) match {
      case JsSuccess(permissionCheck,_) => if(accountSettingsDao.isAllowed(permissionCheck)) Ok else BadRequest("transaction not allowed")
    }
  }
}


