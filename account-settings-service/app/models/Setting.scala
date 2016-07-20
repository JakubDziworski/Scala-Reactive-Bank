package models

import java.math.BigInteger

import play.api.libs.json._
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._
/**
  * Created by kuba on 31.05.16.
  */

case class Setting(accountId:Long, transactionValueLimit: Option[BigDecimal] = None)

object Setting {
  implicit val reads = Json.reads[Setting]
  implicit val writes = Json.writes[Setting]
}