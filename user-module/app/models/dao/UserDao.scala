package models.dao

import com.typesafe.scalalogging.{LazyLogging, Logger}
import models.User
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
  * Created by kuba on 26.05.16.
  */

class UserDao extends LazyLogging {
  val users = mutable.ListBuffer[User]()

  def save (user: User): User = {
    logger.info("adding user {}",user)
    val userWithId = User.create(Some(users.size + 1), user)
    users += userWithId
    userWithId
  }

  def findByEmail(userEmail: String):Option[User] = {
    users.find(_.email == userEmail)
  }
}


