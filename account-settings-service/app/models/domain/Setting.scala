package models.domain

import play.api.libs.json._
/**
  * Created by kuba on 31.05.16.
  */

case class Setting(accountId:Long, transactionValueLimit: Option[BigDecimal] = None)

object Setting {
  implicit val reads = Json.reads[Setting]
  implicit val writes = Json.writes[Setting]
}
