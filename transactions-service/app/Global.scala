import exceptions.ApplicationException
import play.api.{GlobalSettings, mvc}
import play.api.mvc.Results
import play.api.mvc.Result

import scala.concurrent.Future


/**
  * Created by kuba on 28.05.16.
  */
object Global extends GlobalSettings {

  override def onError(request: mvc.RequestHeader, ex: Throwable): Future[Result] = {
    Future.successful(ex match {
      case appException: ApplicationException => appException.getHttpResponse
      case ex : Throwable => Results.BadRequest("Unexpected problem occured "+ ex.getMessage)
    })
  }
}
