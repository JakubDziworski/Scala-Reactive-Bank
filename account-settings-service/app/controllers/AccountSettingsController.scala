package controllers

import javax.inject.Inject

import com.typesafe.scalalogging.LazyLogging
import io.swagger.annotations.{ApiImplicitParams, _}
import models.{PermissionCheck, Settings}
import models.dao.AccountSettingsDao
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

/**
  * Created by kuba on 25.05.16.
  */

@Api("Account Settings Service")
case class AccountSettingsController @Inject()(accountSettingsDao: AccountSettingsDao) extends Controller with LazyLogging {

  @ApiOperation(value = "Tests if API is reachable")
  def test = Action {
    Ok("Reactive Bank - Account Settings Service - REACHABLE")
  }

  @ApiOperation(value = "Sets settings")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "settings", dataType = "models.Settings", required = true, paramType = "body")))
  def setSettings = Action(parse.json) { request =>
    Json.fromJson[Settings](request.body) match {
      case JsSuccess(newSettings, _) => {
        accountSettingsDao.saveSettings(newSettings)
        Ok("Sucesfully changed settings")
      }
    }
  }


  @ApiOperation(value = "Checks permissions")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "checkPermissions", dataType = "models.PermissionCheck", required = true, paramType = "body")))
  def checkPermissions = Action(parse.json) {
    implicit def tuplesWrites = Json.reads[PermissionCheck]
    request => Json.fromJson[PermissionCheck](request.body) match {
      case JsSuccess(permissionCheck,_) =>
        if(accountSettingsDao.isAllowed(permissionCheck)) {
          Ok("Transaction allowed")
        } else {
          BadRequest("Transaction not allowed - limit exceeded")
        }
    }
  }
}


