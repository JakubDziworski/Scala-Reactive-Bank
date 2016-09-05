package actors

import akka.actor.Actor
import akka.actor.Status.Failure
import exceptions.TransactionNotAwaitingVerificationException
import models.dao.TransactionDao

/**
  * Created by kuba on 05.09.16.
  */
class TransactionCancellingActor(dao:TransactionDao) extends Actor {

  override def receive: Receive = {
    case CancelTransaction(transactionId) => cancelTransaction(transactionId)
  }

  def cancelTransaction(transactionId: Long) = {
    if (dao.isAwaitingVerification(transactionId)) {
      sender ! "Transaction sucesfully cancelled"
      dao.cancelTransaction(transactionId)
    } else sender ! Failure(TransactionNotAwaitingVerificationException(transactionId))
  }

}
