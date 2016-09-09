package actors

import models.{Transaction, TransactionSmsCode}

/**
  * Created by kuba on 09.09.16.
  */
object messages {
  case class StartTransaction(transaction: Transaction)

  case class VerifyTransactionWithSms(smsVerifcation: TransactionSmsCode)

  case class CancelTransaction(transactionId: String)

  case class TransactionCancelled()

  case class TransactionVerified()

  case class TransactionStarted(transactionSmsCode: TransactionSmsCode)
}
