import exceptions.ApplicationException
import models.Transaction
import models.dao.Transactions
import play.api.mvc.{Result, Results}
import play.api.{Application, GlobalSettings, mvc}
import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


/**
  * Created by kuba on 28.05.16.
  */
object Global extends GlobalSettings {

  override def onError(request: mvc.RequestHeader, ex: Throwable): Future[Result] = {
    Future.successful(ex match {
      case appException: ApplicationException => appException.getHttpResponse
      case ex: Throwable => Results.BadRequest("Unknown error has occured :" + ex.getMessage)
    })
  }


}