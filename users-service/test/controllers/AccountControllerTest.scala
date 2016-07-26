package controllers

import models.Account
import models.dao.AccountDao
import org.specs2.mock.Mockito
import play.api.libs.json.Json
import play.api.test.{FakeRequest, PlaySpecification}
import scala.concurrent.Await
import scala.concurrent.duration._


/**
  * Created by kuba on 25.05.16.
  */
object AccountControllerTest extends PlaySpecification with Mockito {

  "Api" should {
    "be reachable" in {
      val dao = mock[AccountDao]
      val accountController = new AccountController(dao)
      val result = accountController.test(FakeRequest())

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      charset(result) must beSome("utf-8")
      contentAsString(result) must contain("Reactive Bank - account module - REACHABLE")
    }
    "add new account" in {
      val dao = mock[AccountDao]
      val accountController = new AccountController(dao)
      val accountJSon = Json.parse("""{"owner":"Andrew Johnson", "balance":25000}""")
      val requestWithBody = FakeRequest(POST, "/").withJsonBody(accountJSon)
      val addAccountAction = accountController.addAccount()

      val result = call(addAccountAction, requestWithBody)

      status(result) mustEqual OK
      contentType(result) must beSome("text/plain")
      Await.result(result,1000 millis)
      there was one(dao).save(Account.create("Andrew Johnson", 25000))
    }
    "get account by id" in {
      val dao = mock[AccountDao]
      val accountController = new AccountController(dao)
      dao.findById(1543) returns Some(Account("Andrew Johnson", 25000, Some(1543)))

      val result = accountController.getAccount(1543).apply(FakeRequest())

      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
      contentAsJson(result) must equalTo(Json.parse("""{"owner":"Andrew Johnson","balance":25000,"id":1543}"""))
      charset(result) must beSome("utf-8")
    }

    "not get account if id does not exists" in {
      val dao = mock[AccountDao]
      val accountController = AccountController(dao)
      dao.findById(1543) returns None

      val result = accountController.getAccount(1543).apply(FakeRequest())

      status(result) must equalTo(BAD_REQUEST)
      contentType(result) must beSome("text/plain")
      charset(result) must beSome("utf-8")
    }
    "deposit cash" in {
      val dao = mock[AccountDao]
      val accountController = new AccountController(dao)
      val account: Some[Account] = Some(Account("Andrew Johnson", 2500, Some(1543)))
      dao.findById(1543) returns account

      val depositValueJson = Json.parse("239")
      val requestWithBody = FakeRequest(POST, "/").withJsonBody(depositValueJson)
      val depositAction = accountController.deposit(1543)
      val result = call(depositAction, requestWithBody)

      contentAsString(result)
      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      charset(result) must beSome("utf-8")
      Await.result(result,1000 millis)
      there was one(dao).changeBalance(account.get, 2739)
      there was one(dao).findById(1543)
      there were noMoreCallsTo(dao)
    }
    "withdraw all cash" in {
      val dao = mock[AccountDao]
      val accountController = new AccountController(dao)
      val account: Some[Account] = Some(Account("Andrew Johnson", 2500, Some(1543)))
      dao.findById(1543) returns account

      val withdrawValueJson = Json.parse("2500")
      val requestWithBody = FakeRequest(POST, "/").withJsonBody(withdrawValueJson)
      val depositAction = accountController.withDraw(1543)
      val result = call(depositAction, requestWithBody)

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      charset(result) must beSome("utf-8")
      Await.result(result,1000 millis)
      there was one(dao).changeBalance(account.get, 0)
      there was one(dao).findById(1543)
      there were noMoreCallsTo(dao)
    }
    "not withdraw cash if no sufficient cash" in {
      val dao = mock[AccountDao]
      val accountController = new AccountController(dao)
      val account: Some[Account] = Some(Account("Andrew Johnson", 2500, Some(1543)))
      dao.findById(1543) returns account

      val withdrawValueJson = Json.parse("99501")
      val requestWithBody = FakeRequest(POST, "/").withJsonBody(withdrawValueJson)
      val depositAction = accountController.withDraw(1543)
      val result = call(depositAction, requestWithBody)

      status(result) must equalTo(BAD_REQUEST)
      contentType(result) must beSome("text/plain")
      charset(result) must beSome("utf-8")
      Await.result(result,1000 millis)
      there was one(dao).findById(1543)
      there were noMoreCallsTo(dao)
    }
  }
}



