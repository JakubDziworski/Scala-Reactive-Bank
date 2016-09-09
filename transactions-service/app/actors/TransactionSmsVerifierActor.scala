package actors

import actors.messages.{TransactionVerified, VerifyTransactionWithSms}
import akka.actor.Actor
import akka.actor.Status.Failure
import exceptions.BadVerificationCodeException
import models.TransactionSmsCode
import models.dao.TransactionDao
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by kuba on 05.09.16.
  */
class TransactionSmsVerifierActor(dao: TransactionDao) extends Actor {

  override def receive: Receive = {
    case VerifyTransactionWithSms(smsVerification) => verifyTransaction(smsVerification)
  }

  def verifyTransaction(verification: TransactionSmsCode) {
    val transactionId = verification.transactionId
    val originSender = sender()
    def completeTransaction() = {
      dao.setTransactionVerified(transactionId).map(_ => originSender ! TransactionVerified())
    }
    dao.getSmsCodeForTransaction(transactionId).map {
      case smsCode if smsCode == verification.smscode => completeTransaction()
      case smsCode => originSender ! Failure(BadVerificationCodeException(verification))
    }.recover {
      case e => originSender ! Failure(e)
    }

  }

}
