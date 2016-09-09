package actors

import actors.messages.{CancelTransaction, TransactionCancelled}
import akka.actor.Actor
import models.dao.TransactionDao
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Created by kuba on 05.09.16.
  */
class TransactionCancellingActor(dao: TransactionDao) extends Actor {

  override def receive: Receive = {
    case CancelTransaction(transactionId) => cancelTransaction(transactionId)
  }

  def cancelTransaction(transactionId: String) : Unit = {
    val originSender = sender()
    dao.setTransactionCancelled(transactionId) onComplete {
      case Success(_) => originSender ! TransactionCancelled()
      case Failure(e) => originSender ! akka.actor.Status.Failure(e)
    }
  }
}
