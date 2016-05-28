package controllers

import org.specs2.mock.Mockito
import play.api.test.PlaySpecification

/**
  * Created by kuba on 25.05.16.
  */
object UserController$Test extends PlaySpecification with Mockito {

//  "Api" should {
//    "should be reachable" in {
//      val userDao = mock[UserDao]
//      val result = controllers.TransactionController(userDao).test(FakeRequest())
//
//      status(result) must equalTo(OK)
//      contentType(result) must beSome("text/plain")
//      charset(result) must beSome("utf-8")
//      contentAsString(result) must contain("Reactive Bank - user account module - REACHABLE")
//    }
//    "should add new user" in {
//      val dao = mock[UserDao]
//      val userJSon = Json.parse("""{"email":"andrew@gmail.com", "name":"Andrew", "surname":"Johnson" }""")
//      val requestWithBody = FakeRequest(POST, "/").withJsonBody(userJSon)
//      val addUserAction = controllers.TransactionController(dao).addUser()
//
//      val result = call(addUserAction, requestWithBody)
//
//      status(result) mustEqual OK
//      contentType(result) must beSome("text/plain")
//
//      there was one(dao).save(User.create("andrew@gmail.com", "Andrew", "Johnson"))
//    }
//    "should get user" in {
//      val dao = mock[UserDao]
//      dao.findByEmail("andrew@gmail.com") returns Some(User.create("andrew@gmail.com", "Andrew", "Johnson"))
//
//      val result = controllers.TransactionController(dao).getUser("andrew@gmail.com").apply(FakeRequest())
//
//      status(result) must equalTo(OK)
//      contentType(result) must beSome("application/json")
//      contentAsJson(result) must equalTo(Json.parse("""{"email":"andrew@gmail.com", "name":"Andrew", "surname":"Johnson" }"""))
//      charset(result) must beSome("utf-8")
//    }
//    "should not get user if does not exists" in {
//      val dao = mock[UserDao]
//      dao.findByEmail("andrew@gmail.com") returns None
//
//      val result = controllers.TransactionController(dao).getUser("andrew@gmail.com").apply(FakeRequest())
//
//      status(result) must equalTo(BAD_REQUEST)
//      contentType(result) must beSome("text/plain")
//      charset(result) must beSome("utf-8")
//    }
//  }
}


