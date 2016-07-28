import exceptions.ApplicationException
import play.api.{mvc, GlobalSettings}
import play.api.mvc.Results
import play.libs.F.Promise
import play.mvc.Http.RequestHeader
import play.api.mvc.Result

import scala.concurrent.Future


/**
  * Created by kuba on 28.05.16.
  */
object Global extends GlobalSettings {

  override def onError(request: mvc.RequestHeader, ex: Throwable): Future[Result] = {
    Future.successful(ex match {
      case appException: ApplicationException => appException.getHttpResponse
      case ex : Throwable => Results.BadRequest("Unknown error has occured :"+ ex.getMessage)
    })
  }
}
