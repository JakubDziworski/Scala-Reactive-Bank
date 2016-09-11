package controllers

import javax.inject.{Inject, Singleton}

import com.typesafe.scalalogging.LazyLogging
import io.swagger.annotations.{ApiImplicitParams, ApiOperation, _}
import models.Account
import models.dao.AccountDao
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller, Result}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by kuba on 25.05.16.
  */

@Api("Account Service")
@Singleton
case class AccountController @Inject()(val accountDao: AccountDao) extends Controller with LazyLogging {

  def test = Action {
    Ok("Reactive Bank - account module - REACHABLE")
  }

  @ApiOperation(value="Add account",notes= "Adds new account")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "account", dataType = "models.Account", required = true, paramType = "body")))
  def addAccount = Action.async(parse.json) { request =>
    Json.fromJson[Account](request.body) match {
      case JsSuccess(account, _) => {
        accountDao.save(account).map(acc => Ok("Created account: " + acc.id))
      }
    }
  }

  @ApiOperation(value="Get account",notes= "Gets account by id")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "account id", required = true)))
  def getAccount(accountId: Long) = Action.async {
    performActionOnAccountWithId(accountId, account => Future(Ok(Json.toJson(account))))
  }

  @ApiOperation(value="Deposit",notes= "Deposits cash to the account")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "deposit value", dataType = "java.math.BigDecimal", required = true, paramType = "body")))
  def deposit(accountId: Long) = Action.async(parse.json) {
    request => Json.fromJson[Long](request.body) match {
      case JsSuccess(depositValue, _) => {
        performActionOnAccountWithId(accountId, account => {
          val newBalance = account.balance + depositValue
          accountDao.changeBalance(account, newBalance).map(_ => Ok("Sucesfully deposited."))
        })
      }
      case JsError(error) => Future(BadRequest("Error during parsing" + error))
    }
  }

  @ApiOperation(value="Deposit",notes= "Withdraws cash from account (if there is sufficient money)")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "withdraw value", dataType = "java.math.BigDecimal", required = true, paramType = "body")))
  def withDraw(accountId: Long) = Action.async(parse.json) {
    request => Json.fromJson[Long](request.body) match {
      case JsSuccess(withdrawValue, _) => {
        def action (account:Account): Future[Result] = {
          val oldBalance: BigDecimal = account.balance
          if (oldBalance < withdrawValue) {
            return Future(BadRequest(s"Account with id $accountId has only $oldBalance in balance. Cannot withdraw $withdrawValue"))
          }
          val newBalance = oldBalance - withdrawValue
          accountDao.changeBalance(account, newBalance).map(_ => Ok("Sucesfully withDrawn."))
        }
        performActionOnAccountWithId(accountId, action)
      }
      case JsError(error) => Future(BadRequest("Error during parsing" + error))
    }
  }

  def performActionOnAccountWithId(accountID: Long, action: Account => Future[Result]): Future[Result] = {
    accountDao.findById(accountID) flatMap {
      case Some(account) => {
        action(account)
      }
      case _ => Future(BadRequest("No account with id '" + accountID + "' found"))
    }
  }
}


