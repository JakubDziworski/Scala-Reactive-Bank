package models.dao

import models.{Transaction, TransactionSmsCode}

import scala.concurrent.Future

/**
  * Created by kuba on 07.09.16.
  */
trait TransactionDao {

  def addTransaction(transaction: Transaction): Future[String]

  def addTransactionSmsCode(smsCode: TransactionSmsCode): Future[Unit]

  def getSmsCodeForTransaction(transactionId: String): Future[String]

  def setTransactionVerified(transactionId: String): Future[Unit]

  def setTransactionCancelled(transactionId: String): Future[Unit]
}
