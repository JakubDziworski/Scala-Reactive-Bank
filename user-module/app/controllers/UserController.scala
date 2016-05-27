package controllers

import javax.inject.{Singleton, Inject}

import com.softwaremill.macwire._
import com.typesafe.scalalogging.LazyLogging
import io.swagger.annotations._
import models.User
import models.dao.UserDao
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}

/**
  * Created by kuba on 25.05.16.
  */

@Api("User Account")
@Singleton
case class UserController @Inject() (val userDao:UserDao) extends Controller with LazyLogging {

  def test = Action {
    Ok("Reactive Bank - user account module - REACHABLE")
  }

  @ApiImplicitParams(Array(new ApiImplicitParam(name = "user", dataType = "models.User", required = true, paramType = "body")))
  def addUser = Action(parse.json) { request =>
    Json.fromJson[User](request.body) match {
      case JsSuccess(usr,_) => {
        val usrWithId = userDao.save(usr)
        Ok("Created user: " + usrWithId)
      }
      case JsError(error) => BadRequest("Unable to create user "+error)
    }
  }

  def getUser(userId:String) = Action {
    implicit val userWrites = Json.writes[User]
    userDao.findByEmail(userId) match {
      case Some(usr) => {
        logger.info("found user {} ",usr)
        Ok(Json.toJson(usr))
      }
      case None => BadRequest("No user for id: '" + userId + "' found")
    }
  }
}


