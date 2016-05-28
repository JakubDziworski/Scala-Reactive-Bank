package models

import play.api.libs.json.Json

/**
  * Created by kuba on 27.05.16.
  */
case class Transaction(from:String, to:String, title:String, cashAmount: BigDecimal, id:Option[Long])

object Transaction {
  implicit val writes = Json.writes[Transaction]
  implicit val reads = Json.reads[Transaction]
}
