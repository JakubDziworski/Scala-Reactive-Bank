package controllers

import javax.inject.Inject

import com.typesafe.scalalogging.LazyLogging
import io.swagger.annotations.{ApiImplicitParams, _}
import models.{PermissionCheck, Setting}
import models.dao.AccountSettingsDao
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by kuba on 25.05.16.
  */

@Api("Account Settings Service")
case class AccountSettingsController @Inject()(accountSettingsDao: AccountSettingsDao) extends Controller with LazyLogging {

  @ApiOperation(value = "Test if API is reachable")
  def test = Action {
    Ok("Reactive Bank - Account Settings Service - REACHABLE")
  }

  @ApiOperation(value = "set settings")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "settings", dataType = "models.Setting", required = true, paramType = "body")))
  def setSettings = Action.async(parse.json) { request =>
    Json.fromJson[Setting](request.body) match {
      case JsSuccess(newSettings, _) => {
        accountSettingsDao.saveSettings(newSettings).map {
          _ => Ok("Sucesfully changed settings")
        }
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
  def checkPermissions = Action.async(parse.json) { request =>
    Json.fromJson[PermissionCheck](request.body) match {
      case JsSuccess(permissionCheck, _) => {
        accountSettingsDao.isAllowed(permissionCheck).map {
          case true => Ok("Transaction allowed")
          case false => BadRequest("Transaction not allowed - limit exceeded")
        }
      }
    }
  }
}


