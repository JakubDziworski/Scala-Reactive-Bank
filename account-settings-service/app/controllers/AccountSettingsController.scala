package controllers

import javax.inject.Inject

import com.typesafe.scalalogging.LazyLogging
import models.dao.SettingsDao
import io.swagger.annotations.{ApiImplicitParams, _}
import models.domain.Setting
import models.domain.dto.Transaction
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{Action, Controller, Request, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by kuba on 25.05.16.
  */

@Api("Account Settings Service")
case class AccountSettingsController @Inject()(accountSettingsDao: SettingsDao) extends Controller with LazyLogging {

  val DEFAULT_LIMIT:BigDecimal = 50000
  implicit def tuplesWrites = Json.reads[Transaction]

  @ApiOperation(value = "Test if API is reachable")
  def test = Action {
    Ok("Reactive Bank - Account Settings Service - REACHABLE")
  }

  @ApiOperation(value = "set settings")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "settings", dataType = "models.domain.Setting", required = true, paramType = "body")))
  def setSettings = Action.async(parse.json) { request =>
    Json.fromJson[Setting](request.body) match {
      case JsSuccess(setting, _) => {
        accountSettingsDao.saveSettings(setting).map {
          _ => Ok("Sucesfully changed settings")
        }
      }
    }
  }

  @ApiOperation(value = "checkPermissions")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "checkPermissions", dataType = "models.domain.dto.Transaction", required = true, paramType = "body")))
  def checkPermissions = Action.async(parse.json) { request =>
    Json.fromJson(request.body) match {
      case JsSuccess(transaction, _) => {
        def transactionVerifier(setting:Setting): Result = {
          val transferValue = transaction.transferValue
          val transferLimit = setting.transactionValueLimit.getOrElse(DEFAULT_LIMIT)
          if (transferValue <= 0) {
            return BadRequest("Cannot transfer non positive value")
          }
          if (transferLimit >= transferValue) {
            return Ok("Transaction allowed")
          }
          return BadRequest("Transaction not allowed - limit exceeded")
        }
        accountSettingsDao.getCurrentSettings(transaction.accountId).map(transactionVerifier)
      }
    }
  }

}


