package models.dao

import slick.driver.H2Driver.api._
import slick.lifted.{ProvenShape, ForeignKeyQuery}
import models.Transaction
import models.TransactionVerification

/**
  * Created by kuba on 27.06.16.
  */

case class Transactions(tag: Tag) extends Table[Transaction](tag, "TRANSACTIONS") {

  def id = column[Long]("TRANSACTION_ID", O.PrimaryKey,O.AutoInc)

  def from = column[String]("FROM")

  def to = column[String]("TO")

  def title = column[String]("TITLE")

  def cashAmount = column[BigDecimal]("CASH_AMOUNT")

  override def * = (from, to, title, cashAmount, id.?) <>((Transaction.apply _).tupled, Transaction.unapply)
}

case class CompletedTransactions(tag: Tag) extends Table[(Long, Long)](tag, "COMPLETED_TRANSACTIONS") {
  def id = column[Long]("ID", O.PrimaryKey,O.AutoInc)

  def transactionId = column[Long]("TRANSACTION_ID")

  override def * : ProvenShape[(Long, Long)] = (id, transactionId)

  def supplier = foreignKey("COMPL_TRANS_TRANSACTION_FK", transactionId, TableQuery[Transactions])(_.id)
}

case class CanceledTransactions(tag: Tag) extends Table[(Long, Long)](tag, "CANCELED_TRANSACTIONS") {
  def id = column[Long]("ID", O.PrimaryKey,O.AutoInc)

  def transactionId = column[Long]("TRANSACTION_ID")

  override def * : ProvenShape[(Long, Long)] = (id, transactionId)

  def supplier = foreignKey("CANC_TRANS_TRANSACTION_FK", transactionId, TableQuery[Transactions])(_.id)
}


case class TransactionVerifications(tag: Tag) extends Table[TransactionVerification](tag, "TRANSACTION_VERIFICATIONS") {

  def id = column[Long]("TRANSACTION_ID", O.PrimaryKey)

  def smsCode = column[String]("SMS_CODE")

  override def * : ProvenShape[TransactionVerification] = (id, smsCode) <>((TransactionVerification.apply _).tupled, TransactionVerification.unapply)

}

