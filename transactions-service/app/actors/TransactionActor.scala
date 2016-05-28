package actors

import javax.inject.Inject

import exceptions.{BadVerificationCodeException, TransactionNotAwaitingVerificationException}
import akka.actor.Actor
import akka.actor.Status.Failure
import models.dao.TransactionDao
import models.{TransactionVerification, Transaction}
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json.Json

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
    val transactionId = dao.getNextAvailableTransactionId
    val transactionWithID = Transaction(transaction.from, transaction.to, transaction.title, transaction.cashAmount, Some(transactionId))
    generateAndSendBackVerificicationCode(transactionWithID)
    dao.addTransaction(transactionWithID)
  }

  def generateAndSendBackVerificicationCode(startTransaction: Transaction) = {
    val smsCode = SmsGenerator.getRandomCode
    val smsVerification = TransactionVerification(startTransaction.id.get, smsCode)
    sender ! smsVerification
    dao addAwaitingVerification smsVerification
  }

  def verifyTransaction(verification: TransactionVerification) = {
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

  def cancelTransaction(transactionId: Long) = {
    if (dao.isAwaitingVerification(transactionId)) {
      sender ! "Transaction sucesfully cancelled"
      dao.cancelTransaction(transactionId)
    } else sender ! Failure(TransactionNotAwaitingVerificationException(transactionId))
  }
}

case class StartTransaction(transaction: Transaction)

case class VerifyTransactionWithSms(smsVerifcation: TransactionVerification)
case class CancelTransaction(transactionId: Long)

object SmsGenerator {
  def getRandomCode:String = {
    (1000 + util.Random.nextInt(9000)).toString
  }
}

// Zlecenie tranzakcji (POST) -> wysłany sms (oczekiwanie na potwierdzenie) -> wysłanie sms (POST) -> tranzakcja zaceptowana
// Zlecenie tranzakcji (POST) -> wysłany sms (oczekiwanie na potwierdzenie) -> odrzucenie tranzakcji (POST) tranzakcaj odrzucona
// Zlecenie tranzakcji (POST) -> wysłany sms (oczekiwanie na potwierdzenie) -> czas minal -> tranzakcaj odrzucona