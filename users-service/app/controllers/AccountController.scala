package controllers

import javax.inject.{Inject, Singleton}

import com.softwaremill.macwire._
import com.typesafe.scalalogging.LazyLogging
import io.swagger.annotations.{ApiImplicitParams, _}
import models.Account
import models.dao.AccountDao
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller, Result}
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by kuba on 25.05.16.
  */

@Api("account Account")
@Singleton
case class AccountController @Inject()(val accountDao: AccountDao) extends Controller with LazyLogging {

  def test = Action {
    Ok("Reactive Bank - account module - REACHABLE")
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "account", dataType = "models.Account", required = true, paramType = "body")))
  def addAccount = Action(parse.json) { request =>
    Json.fromJson[Account](request.body) match {
      case JsSuccess(account, _) => {
        val usrWithId = accountDao.save(account)
        Ok("Created account: " + usrWithId)
      }
      case JsError(error) => BadRequest("Unable to create account " + error)
    }
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "account id", required = true)))
  def getAccount(accountId: Long) = Action {
    performActionOnAccountWithId(accountId, account => {
      logger.info("found account {} ", account)
      Ok(Json.toJson(account))
    })
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "deposit value", dataType = "java.math.BigDecimal", required = true, paramType = "body")))
  def deposit(accountId: Long) = Action(parse.json) {
    request => Json.fromJson[Long](request.body) match {
      case JsSuccess(depositValue, _) => {
        performActionOnAccountWithId(accountId, account => {
          val newBalance = account.balance + depositValue
          accountDao.changeBalance(account, newBalance)
          Ok("Sucesfully deposited.")
        })
      }
      case JsError(error) => BadRequest("Error during parsing" + error)
    }
  }



  @ApiImplicitParams(Array(new ApiImplicitParam(name = "withdraw value", dataType = "java.math.BigDecimal", required = true, paramType = "body")))
  def withDraw(accountId: Long) = Action(parse.json) {
    request => Json.fromJson[Long](request.body) match {
      case JsSuccess(withdrawValue, _) => {
        def action (account:Account): Result = {
          val oldBalance: BigDecimal = account.balance
          if (oldBalance < withdrawValue) {
            return BadRequest(s"Account with id $accountId has only $oldBalance in balance. Cannot withdraw $withdrawValue")
          }
          val newBalance = oldBalance - withdrawValue
          accountDao.changeBalance(account, newBalance)
          return Ok("Sucesfully withDrawn.")
        }
        performActionOnAccountWithId(accountId, action)
      }
      case JsError(error) => BadRequest("Error during parsing" + error)
    }
  }

  def performActionOnAccountWithId(accountID: Long, action: Account => Result): Result = {
    accountDao.findById(accountID) match {
      case Some(account) => {
        action(account)
      }
      case _ => BadRequest("No account with id '" + accountID + "' found")
    }
  }
}


