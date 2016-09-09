package controllers

import javax.inject.Inject

import actors._
import actors.messages._
import akka.actor.ActorDSL._
import akka.actor.{ActorSystem, Props}
import com.typesafe.scalalogging.LazyLogging
import io.swagger.annotations._
import models.dao.TransactionDao
import models.{Transaction, TransactionSmsCode}
import play.Play
import play.api.http.{HeaderNames, MimeTypes}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsSuccess, Json}
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.mvc.{Action, Controller, Result}

import scala.concurrent.Future

/**
  * Created by kuba on 25.05.16.
  */

@Api("Transaction Service")
case class TransactionController @Inject()(messagesApi: MessagesApi, ws: WSClient, transactionDao: TransactionDao) extends Controller with LazyLogging with I18nSupport {

  implicit val actorSystem = ActorSystem("Transactions")
  val transactionStartingActor = actorSystem.actorOf(Props(new TransactionStartingActor(transactionDao)))
  val transactionCancellingActor = actorSystem.actorOf(Props(new TransactionCancellingActor(transactionDao)))
  val transactionSmsVerifierActor = actorSystem.actorOf(Props(new TransactionSmsVerifierActor(transactionDao)))

  @ApiOperation(value = "Test if API is reachable", notes = "Test if API is reachable")
  def test = Action {
    Ok("Reactive Bank - Transactions Service - REACHABLE")
  }

  @ApiOperation(value = "Trigger transaction", notes = "Starts transaction. Returns sms code to provide in verification (/verify)")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "transaction", dataType = "models.Transaction", required = true, paramType = "body")))
  def startTransaction = Action.async(parse.json) { request =>
    Json.fromJson[Transaction](request.body) match {
      case JsSuccess(transaction, _) => {
        val permissionsResponse: Future[WSResponse] = getPermissions(transaction)
        permissionsResponse.map {
          case response if response.status == OK => processTransaction(transaction)
          case response if response.status == NOT_FOUND => BadRequest(Messages("http.response.permissions.error"))
        }
      }
    }
  }


  @ApiOperation(value = "Verify", notes = "Verify triggered transaction with sms code provided from '/start' request")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "smsVerification", dataType = "models.TransactionSmsCode", required = true, paramType = "body")))
  def verifyTransactionWithSms = Action(parse.json) {
    request => Json.fromJson[TransactionSmsCode](request.body) match {
      case JsSuccess(sms, _) => {
        implicit val i = inbox()
        transactionSmsVerifierActor ! VerifyTransactionWithSms(sms)
        i.receive() match {
          case TransactionVerified() => Ok(Messages("http.response.transaction.verified", sms.transactionId))
        }
      }
    }
  }

  @ApiOperation(value = "Cancel transaction", notes = "Cancel transaction, which waits for verification (sms code)")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "transactionId", dataType = "String", required = true, paramType = "body")))
  def cancelTransaction = Action(parse.json) {
    request => Json.fromJson[String](request.body) match {
      case JsSuccess(transactionId, _) => {
        implicit val i = inbox()
        transactionCancellingActor ! CancelTransaction(transactionId)
        i.receive() match {
          case TransactionCancelled() => Ok(Messages("http.response.transaction.cancelled", transactionId))
        }
      }
    }
  }

  private def processTransaction(transaction: Transaction): Result = {
    implicit val i = inbox()
    transactionStartingActor ! StartTransaction(transaction)
    i.receive() match {
      case TransactionStarted(sms) => Ok(Json.toJson[TransactionSmsCode](sms))
    }
  }

  private def getPermissions(transaction: Transaction): Future[WSResponse] = {
    val permissionUrl = Play.application().configuration().getString("settings.service.permission.check.url")
    val payload = Json.obj("accountId" -> transaction.from, "transferValue" -> transaction.cashAmount)
    ws.url(permissionUrl)
      .withHeaders(HeaderNames.CONTENT_TYPE -> MimeTypes.JSON)
      .post(payload.toString)
  }
}


