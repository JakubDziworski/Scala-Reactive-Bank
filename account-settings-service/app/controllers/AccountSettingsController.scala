package controllers

import javax.inject.Inject

import com.typesafe.scalalogging.LazyLogging
import io.swagger.annotations.{ApiImplicitParams, _}
import models.dao.SettingsDao
import models.domain.Setting
import models.domain.dto.Transaction
import play.api.libs.json._
import play.api.mvc.{Action, Controller, Result}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by kuba on 25.05.16.
  */

@Api("Account Settings Service")
case class AccountSettingsController @Inject()(accountSettingsDao: SettingsDao) extends Controller with LazyLogging {

  val DEFAULT_LIMIT:BigDecimal = 50000
  implicit def tuplesWrites = Json.reads[Transaction]

  @ApiOperation(value = "test",notes="Checks if API is reachable")
  def test = Action {
    Ok("Reactive Bank - Account Settings Service - REACHABLE")
  }

  @ApiOperation(value = "set settings",notes = "Sets money value limit for transaction")
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

  @ApiOperation(value = "checkPermissions",notes = "Checks if the transaction value is not higher than the settings limit")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "checkPermissions", dataType = "models.domain.dto.Transaction", required = true, paramType = "body")))
  def checkPermissions = Action.async(parse.json) { request =>
    Json.fromJson(request.body) match {
      case JsSuccess(transaction, _) => {
        def transactionVerifier(setting:Setting): Result = {
          val transferValue = transaction.transferValue
          val transferLimit = setting.transactionValueLimit.getOrElse(DEFAULT_LIMIT)
          transferValue match {
            case t if t <= 0 => BadRequest("Cannot transfer non positive value")
            case t if t > transferLimit => BadRequest("Transaction not allowed - limit exceeded")
            case _ => Ok("Transaction allowed")
          }
        }
        accountSettingsDao.getCurrentSettings(transaction.accountId).map(transactionVerifier)
      }
    }
  }

}


