package actors

import java.util.UUID

import actors.messages.{StartTransaction, TransactionStarted}
import akka.actor.Actor
import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator
import models.dao.TransactionDao
import models.{Transaction, TransactionSmsCode}

import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by kuba on 05.09.16.
  */
class TransactionStartingActor(dao: TransactionDao) extends Actor {

  override def receive: Receive = {
    case StartTransaction(transaction) => startTransaction(transaction)
  }

  def startTransaction(transaction: Transaction): Future[Unit] = {
    val smsCode = SmsGenerator.getRandomCode
    val originSender = sender()
    dao.addTransaction(transaction).map { transactionId =>
      val transactionSmsCode = TransactionSmsCode(transactionId, smsCode)
      dao.addTransactionSmsCode(transactionSmsCode)
      originSender ! TransactionStarted(transactionSmsCode)
    }.recover {
      case e => originSender ! akka.actor.Status.Failure(e)
    }
  }

  object SmsGenerator {
    def getRandomCode: String = {
      (1000 + util.Random.nextInt(9000)).toString
    }
  }

}
