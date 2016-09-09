package models.dao.mongodb

import javax.inject.Inject

import com.google.inject.Singleton
import com.typesafe.scalalogging.LazyLogging
import exceptions.TransactionNotAwaitingVerificationException
import models.dao.TransactionDao
import models.{Transaction, TransactionSmsCode}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.Future


/**
  * Created by kuba on 26.05.16.
  */
@Singleton
class TransactionMongoDao @Inject()(val mongo: ReactiveMongoApi) extends TransactionDao with LazyLogging {

  val TransactionStarted = "started"
  val TransactionVerified = "verified"
  val TransactionCancelled = "cancelled"

  lazy val transactions = mongo.database.map(_.collection[JSONCollection]("transactions"))
  lazy val transactionsSmsCodes = mongo.database.map(_.collection[JSONCollection]("transactionsSmsCodes"))

  override def addTransaction(transaction: Transaction): Future[String] = {
    logger.debug("Adding transaction {} ", transaction)
    val doc = transactionToDocument(transaction)
    transactions.flatMap(_.insert[BSONDocument](doc)).map {
      _ => doc.getAs[BSONObjectID]("_id").get.stringify
    }
  }

  override def addTransactionSmsCode(smsCode: TransactionSmsCode): Future[Unit] = {
    logger.debug("Adding transaction sms code{} ", smsCode)
    transactionsSmsCodes.map(_.insert(smsCode))
  }

  override def getSmsCodeForTransaction(transactionId: String):  Future[String] = {
    val byId = BSONDocument("transactionId" -> transactionId)
    val projection = BSONDocument("smscode" -> true,"_id" -> false)
    transactionsSmsCodes.flatMap(_.find(byId, projection).one[BSONDocument]).map {
      case Some(doc) => doc.getAs[String]("smscode").get
      case None => throw TransactionNotAwaitingVerificationException(transactionId)
    }
  }

  override def setTransactionVerified(transactionId: String): Future[Unit] = {
    updateTransactionStatus(transactionId, TransactionVerified)
  }

 override def setTransactionCancelled(transactionId: String): Future[Unit] = {
    updateTransactionStatus(transactionId, TransactionCancelled)
  }

  private def updateTransactionStatus(transactionId: String, newStatus: String): Future[Unit] = {
    val selector = BSONDocument("_id" -> BSONObjectID(transactionId),"status" -> TransactionStarted)
    val modifier = BSONDocument("$set" -> BSONDocument("status" -> newStatus))
    transactions.flatMap(_.update(selector, modifier)).map {
      case res if res.nModified == 1 => Future.successful()
      case res if res.errmsg.isDefined => throw new RuntimeException(res.errmsg.get)
      case _ => throw TransactionNotAwaitingVerificationException(transactionId)
    }
  }

  private def transactionToDocument(t: Transaction): BSONDocument = BSONDocument(
    "_id" -> BSONObjectID.generate(),
    "to" -> t.to,
    "from" -> t.from,
    "title" -> t.title,
    "cashAmount" -> t.cashAmount.doubleValue(),
    "status" -> TransactionStarted
  )
}




