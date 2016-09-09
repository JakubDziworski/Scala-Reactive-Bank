package exceptions

import models.{TransactionSmsCode, TransactionSmsCode$}
import play.api.mvc.{Result, Results}

/**
  * Created by kuba on 28.05.16.
  */
abstract class ApplicationException extends RuntimeException {
  def getHttpResponse : Result
  override def getMessage: String = getHttpResponse.body.toString
}

case class TransactionNotAwaitingVerificationException(transactionId: String) extends ApplicationException {
  override def getHttpResponse = {
    Results.BadRequest("Transaction ( id = " + transactionId + ") is not waiting for verification ")
  }
}

case class BadVerificationCodeException(verification: TransactionSmsCode) extends ApplicationException{
  override def getHttpResponse = {
    Results.BadRequest("Code (" + verification.smscode + ") is wrong for transaction id:" + verification.transactionId)
  }
}
