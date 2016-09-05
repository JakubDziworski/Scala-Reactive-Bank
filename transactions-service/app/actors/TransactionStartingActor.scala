package actors

import akka.actor.Actor
import models.dao.TransactionDao
import models.{Transaction, TransactionVerification}

/**
  * Created by kuba on 05.09.16.
  */
class TransactionStartingActor(dao:TransactionDao) extends Actor {

  override def receive: Receive = {
    case StartTransaction(transaction) => startTransaction(transaction)
  }

  def startTransaction(transaction: Transaction) = {
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

  object SmsGenerator {
    def getRandomCode: String = {
      (1000 + util.Random.nextInt(9000)).toString
    }
  }

}
