package exceptions

import models.{TransactionVerification}
import play.api.mvc.{Results, Result}

/**
  * Created by kuba on 28.05.16.
  */
abstract class ApplicationException extends RuntimeException {
  def getHttpResponse : Result
  override def getMessage: String = getHttpResponse.body.toString
}

case class TransactionNotAwaitingVerificationException(transactionId: Long) extends ApplicationException {
  override def getHttpResponse = {
    Results.BadRequest("Transaction ( id = " + transactionId + ") is not waiting for verification ")
  }
}

case class BadVerificationCodeException(verification: TransactionVerification) extends ApplicationException{
  override def getHttpResponse = {
    Results.BadRequest("Code (" + verification.smscode + ") is wrong for transaction id:" + verification.transactionId)
  }
}
