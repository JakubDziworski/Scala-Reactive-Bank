package controllers

import javax.inject.Inject

import actors.{CancelTransaction, StartTransaction, TransactionActor, VerifyTransactionWithSms}
import akka.actor.ActorDSL._
import akka.actor.{ActorDSL, ActorSystem, Props}
import com.typesafe.scalalogging.LazyLogging
import io.swagger.annotations._
import models.dao.TransactionDao
import models.{TransactionVerification, Transaction}
import play.api.libs.json.{JsSuccess, Json}
import play.api.mvc.{AnyContent, Action, Controller}
import play.mvc.Http.Request
import play.mvc.Result
import akka.pattern.ask

import scala.concurrent.Future

/**
  * Created by kuba on 25.05.16.
  */

@Api("Transaction Service")
case class TransactionController @Inject() (transactionDao: TransactionDao) extends Controller with LazyLogging {

  implicit val actorSystem = ActorSystem("Transactions")
  val transactionActor = actorSystem.actorOf(Props(new TransactionActor(transactionDao)))

  @ApiOperation(value = "Test if API is reachable")
  def test = Action {
    Ok("Reactive Bank - Transactions Service - REACHABLE")
  }

  @ApiOperation(value = "Trigger transaction")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "transaction", dataType = "models.Transaction", required = true, paramType = "body")))
  def startTransaction = Action(parse.json) { request =>
    Json.fromJson[Transaction](request.body) match {
      case JsSuccess(transaction,_) => {
        implicit val i = inbox()
        transactionActor ! StartTransaction(transaction)
        i.receive() match {
          case sms:TransactionVerification => Ok(Json.toJson[TransactionVerification](sms))
        }
      }
    }
  }

  @ApiOperation(value = "Verify triggered transaction with sms code provided from 'start' request")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "smsVerification", dataType = "models.TransactionVerification", required = true, paramType = "body")))
  def verifyTransactionWithSms = Action(parse.json) {
    request => Json.fromJson[TransactionVerification](request.body) match {
      case JsSuccess(sms,_) => {
        implicit val i = inbox()
        transactionActor ! VerifyTransactionWithSms(sms)
        i.receive() match {
          case str:String => Ok(str)
          case _ => throw new RuntimeException("Unknown error")
        }
      }
    }
  }

  @ApiOperation(value = "Cancel transaction, which waits for verification (sms code)")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "transactionId",dataType = "Long", required = true, paramType = "body")))
  def cancelTransaction = Action(parse.json) {
    request => Json.fromJson[Long](request.body) match {
      case JsSuccess(transactionId,_) => {
        implicit val i = inbox()
        transactionActor ! CancelTransaction(transactionId)
        i.receive() match {
          case str:String => Ok(str)
          case _ => throw new RuntimeException("Unknown error")
        }
      }
    }
  }
}


