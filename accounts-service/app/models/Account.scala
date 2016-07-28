package models

import play.api.libs.json.Json

/**
  * Created by kuba on 25.05.16.
  */
case class Account(owner:String,balance: BigDecimal, id: Option[Long] = None) {

}

object Account {
  implicit val accountWrites = Json.writes[Account]
  implicit val accountReads = Json.reads[Account]

  def create (owner:String,balance: BigDecimal): Account = {
    Account(owner,balance)
  }

  def create(id:Option[Long], account:Account): Account = {
    Account(account.owner,account.balance,id)
  }
}
