package actors

import akka.actor.Actor
import akka.actor.Status.Failure
import exceptions.{BadVerificationCodeException, TransactionNotAwaitingVerificationException}
import models.TransactionVerification
import models.dao.TransactionDao

/**
  * Created by kuba on 05.09.16.
  */
class TransactionSmsVerifierActor(dao: TransactionDao) extends Actor {

  override def receive: Receive = {
    case VerifyTransactionWithSms(smsVerification) => verifyTransaction(smsVerification)
  }

  def verifyTransaction(verification: TransactionVerification) {
    if (dao.isAwaitingVerification(verification.transactionId)) {
      dao.getVerificationCodeForTransaction(verification.transactionId) match {
        case Some(code) => {
          if (code == verification.smscode) {
            sender ! "Transaction sucesfully verified"
            dao markTransactionToCompleted (verification)
          } else {
            sender ! Failure(BadVerificationCodeException(verification))
          }
        }
      }
    } else {
      val exception = TransactionNotAwaitingVerificationException(verification.transactionId)
      sender ! Failure(exception)
    }
  }

}
