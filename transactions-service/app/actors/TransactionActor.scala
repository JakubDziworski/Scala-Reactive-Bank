package actors

import javax.inject.Inject

import exceptions.{BadVerificationCodeException, TransactionNotAwaitingVerificationException}
import akka.actor.Actor
import akka.actor.Status.Failure
import models.dao.TransactionDao
import models.{Transaction, TransactionVerification}
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global


import scala.concurrent.Future

/**
  * Created by kuba on 27.05.16.
  */
case class TransactionActor(dao: TransactionDao) extends Actor {

  override def receive = {
    case StartTransaction(transaction) => stratTransaction(transaction)
    case VerifyTransactionWithSms(smsVerification) => verifyTransaction(smsVerification)
    case CancelTransaction(transactionId) => cancelTransaction(transactionId)
  }

  def stratTransaction(transaction: Transaction) = {
    dao.addTransaction(transaction)
    generateAndSendBackVerificicationCode(transaction)
  }

  //to powinno byc w nowym aktorze
  //moze byc wyorzystane do timeoutowania
  def generateAndSendBackVerificicationCode(startTransaction: Transaction) = {
    val smsCode = SmsGenerator.getRandomCode
    val smsVerification = TransactionVerification(startTransaction.id.get, smsCode)
    sender ! smsVerification
    dao addAwaitingVerification smsVerification
  }

  def verifyTransaction(verification: TransactionVerification) = {
    dao.getVerificationCodeForTransaction(verification.transactionId).onSuccess {
      case code => {
        if (code == verification.smscode) {
          sender ! "Transaction sucesfully verified"
          dao markTransactionToCompleted (verification)
        } else {
          sender ! Failure(BadVerificationCodeException(verification))
        }
      }
    }
  }

  def cancelTransaction(transactionId: Long) = {
    val cancelTransactionFuture: Future[Unit] = dao.cancelTransaction(transactionId)
    cancelTransactionFuture.onSuccess {
      case _ => sender ! "Transaction sucesfully cancelled"
    }
    cancelTransactionFuture.onFailure {
      case _ => sender ! "Canceling transaction failed"
    }
  }
}

case class StartTransaction(transaction: Transaction)

case class VerifyTransactionWithSms(smsVerifcation: TransactionVerification)

case class CancelTransaction(transactionId: Long)

object SmsGenerator {
  def getRandomCode: String = {
    (1000 + util.Random.nextInt(9000)).toString
  }
}

// Zlecenie tranzakcji (POST) -> wysłany sms (oczekiwanie na potwierdzenie) -> wysłanie sms (POST) -> tranzakcja zaceptowana
// Zlecenie tranzakcji (POST) -> wysłany sms (oczekiwanie na potwierdzenie) -> odrzucenie tranzakcji (POST) tranzakcaj odrzucona
// Zlecenie tranzakcji (POST) -> wysłany sms (oczekiwanie na potwierdzenie) -> czas minal -> tranzakcaj odrzucona