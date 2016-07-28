package models.dao


import javax.inject.Singleton

import com.typesafe.scalalogging.LazyLogging
import models.{TransactionVerification, Transaction}

import scala.collection.mutable

/**
  * Created by kuba on 26.05.16.
  */
@Singleton
class TransactionDao extends LazyLogging {
  val transactions = mutable.ListBuffer[Transaction]()

  val comletedTransactions = mutable.ListBuffer[Transaction]()
  val canceledTransasctions = mutable.ListBuffer[Transaction]()
  val awaitingVerification = mutable.ListBuffer[TransactionVerification]()
  def getNextAvailableTransactionId : Long = transactions.size+1

  def addTransaction(transaction: Transaction) = {
    transactions+=transaction
    logger.debug("Added transaction {}",transaction)
  }

  def addAwaitingVerification(smsVerifcation: TransactionVerification) : Unit = {
    awaitingVerification += smsVerifcation
    logger.debug("Added Transaction Awaiting Verification {} ",smsVerifcation)
  }

  def getVerificationCodeForTransaction(transactionId:Long) : Option[String] = {
    awaitingVerification.find(_.transactionId == transactionId).map(_.smscode)
  }


  def markTransactionToCompleted(smsVerifcation: TransactionVerification) : Unit = {
    isAwaitingVerification(smsVerifcation.transactionId) match {
      case true => {
        awaitingVerification -= smsVerifcation
        val transaction: Transaction = (transactions find (_.id.get == smsVerifcation.transactionId)).get
        comletedTransactions += transaction
        logger.debug("Transaction ({}) Verified ({}) ",smsVerifcation,transaction)
      }
      case false => throw new RuntimeException("cannot pop non existing element")
    }
  }

  def cancelTransaction(transactionId: Long) = {
    awaitingVerification find(_.transactionId == transactionId) match {
      case Some(transaction) =>  {
        awaitingVerification -= transaction
        canceledTransasctions += (transactions find (_.id == transaction.transactionId)).get
        logger.debug("Canceled Transaction {} ",transaction)
      }
      case None => throw new RuntimeException("cannot cancel non awaiting transaction")
    }
  }

  def isAwaitingVerification(transactionId:Long) = awaitingVerification.exists(_.transactionId == transactionId)
}


