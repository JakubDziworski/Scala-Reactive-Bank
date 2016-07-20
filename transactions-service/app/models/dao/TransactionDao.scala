package models.dao


import java.util.concurrent.TimeUnit
import javax.inject.Singleton

import com.typesafe.scalalogging.LazyLogging
import models.{Transaction, TransactionVerification}
import slick.lifted.TableQuery

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by kuba on 26.05.16.
  */
@Singleton
class TransactionDao extends LazyLogging {

  val transactions = TableQuery[Transactions]
  val comletedTransactions = TableQuery[CompletedTransactions]
  val canceledTransasctions = TableQuery[CanceledTransactions]
  val awaitingVerification = TableQuery[TransactionVerifications]

  val db = Database.forConfig("h2mem1")
  Await.result(db.run(MTable.getTables).flatMap {
    case tables if tables.isEmpty => {
      val schema = transactions.schema ++ comletedTransactions.schema ++ canceledTransasctions.schema
      db.run(schema.create)
    }
    case _ => Future.successful("schema already created")
  }, Duration.Inf)

  def addTransaction(transaction: Transaction): Future[Unit] = {
    db.run(
      def insertedTransaction = (transactions returning transactions.map(_.id)) += transaction
    ).map(_ => logger.debug("Added transaction {}", transaction))
  }

  def addAwaitingVerification(smsVerifcation: TransactionVerification): Future[Unit] = {
    db.run(
      awaitingVerification += smsVerifcation
    ).map(_ => logger.debug("Added Transaction Awaiting Verification {} ", smsVerifcation))
  }

  def getVerificationCodeForTransaction(transactionId: Long): Future[String] = {
    db.run(awaitingVerification.filter(_.id === transactionId).map(_.smsCode).result.head)
  }


  def markTransactionToCompleted(smsVerifcation: TransactionVerification): Future[Unit] = {
    db.run(DBIO.seq(
      awaitingVerification.filter(_.id === smsVerifcation.transactionId).delete,
      comletedTransactions.map(t => (t.transactionId)) += smsVerifcation.transactionId
    ))
  }

  def cancelTransaction(transactionId: Long): Future[Unit] = {
    db.run(DBIO.seq(
      awaitingVerification.filter(_.id === transactionId).delete,
      canceledTransasctions.map(t => (t.transactionId)) += transactionId
    ))
  }
}


