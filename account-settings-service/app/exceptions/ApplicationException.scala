package exceptions

import play.api.mvc.{Results, Result}

/**
  * Created by kuba on 28.05.16.
  */
abstract class ApplicationException extends RuntimeException {
  def getHttpResponse : Result
  override def getMessage: String = getHttpResponse.body.toString
}

case class SettingForIdDoesNotExistException(accountId: Long) extends  ApplicationException {
  override def getHttpResponse: Result = Results.BadRequest(s"There are no settings for accountid = $accountId")
}

