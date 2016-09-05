package actors

import models.{Transaction, TransactionVerification}

/**
  * Created by kuba on 05.09.16.
  */
  case class StartTransaction(transaction: Transaction)

  case class VerifyTransactionWithSms(smsVerifcation: TransactionVerification)

  case class CancelTransaction(transactionId: Long)
