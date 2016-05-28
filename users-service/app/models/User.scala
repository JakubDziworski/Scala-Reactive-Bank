package models

import play.api.libs.json.Json

/**
  * Created by kuba on 25.05.16.
  */
case class User(email:String,name:String,surname:String,id: Option[Long] = None) {

}

object User {
  implicit val userWrites = Json.writes[User]
  implicit val userReads = Json.reads[User]

  def create (email:String,name:String,surname:String): User = {
    User(email,name,surname)
  }

  def create(id:Option[Long],user:User): User = {
    User(user.email,user.name,user.surname,id)
  }
}
