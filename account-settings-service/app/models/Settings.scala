package models

import java.math.BigInteger

import play.api.libs.json._
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._
/**
  * Created by kuba on 31.05.16.
  */

case class Settings(accountId:Long, transactionValueLimit: Option[BigDecimal] = None, transactionsPerDay:Option[Int])

object Settings {
  implicit val reads = Json.reads[Settings]
  implicit val writes = Json.writes[Settings]

  def merge(oldSettings:Settings,newSettings:Settings): Settings = {
    val maxTransactionValue = newSettings.transactionValueLimit.orElse(oldSettings.transactionValueLimit)
    val transactionsPerDay = newSettings.transactionsPerDay.orElse(oldSettings.transactionsPerDay)
    Settings(newSettings.accountId,maxTransactionValue,transactionsPerDay)
  }
}