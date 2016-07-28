package controllers

import models.dao.SettingsDao
import models.domain.Setting
import org.specs2.mock.Mockito
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.{FakeRequest, PlaySpecification}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}



/**
  * Created by kuba on 26.07.16.
  */
class AccountSettingsControllerTest extends PlaySpecification with Mockito {
  "Api" should {
    "allow transaction" in {
      assertTransactionAllowed(1,1)
      assertTransactionAllowed(1,2)
      assertTransactionAllowed(100,500)
      assertTransactionAllowed(125,315)
    }
    "not allow transaction" in {
      assertTransactionNotAllowed(-1,-5)
      assertTransactionNotAllowed(0,0)
      assertTransactionNotAllowed(0,5)
      assertTransactionNotAllowed(0,1)
      assertTransactionNotAllowed(1,0)
      assertTransactionNotAllowed(2,1)
    }
  }

  def assertTransactionAllowed(transactionValue:BigDecimal,transactionLimit:BigDecimal) = {
    val result: Future[Result] = assertTransactionResult(transactionValue, transactionLimit)
    status(result) mustEqual OK
  }

  def assertTransactionNotAllowed(transactionValue:BigDecimal, transactionLimit:BigDecimal) = {
    val result: Future[Result] = assertTransactionResult(transactionValue, transactionLimit)
    status(result) mustEqual BAD_REQUEST
  }

  def assertTransactionResult(transactionValue: BigDecimal, transactionLimit: BigDecimal): Future[Result] = {
    val dao = mock[SettingsDao]
    dao.getCurrentSettings(14312) returns Future(Setting(14312, Some(transactionLimit)))

    val controller = AccountSettingsController(dao)
    val transactionJson = Json.parse(s"""{"accountId":14312, "transferValue":$transactionValue}""")
    val requestWithBody = FakeRequest(POST, "/").withJsonBody(transactionJson)
    val action = controller.checkPermissions()

    val result = call(action, requestWithBody)

    contentType(result) must beSome("text/plain")
    Await.result(result, 1000 millis)
    there was one(dao).getCurrentSettings(14312)
    there were noMoreCallsTo(dao)
    result
  }
}
