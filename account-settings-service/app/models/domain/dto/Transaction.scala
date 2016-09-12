package models.domain.dto

import play.api.libs.json.{Json, Reads}

/**
  * Created by kuba on 28.06.16.
  */
case class Transaction(accountId:Long, transferValue:BigDecimal)
object Transaction {
  implicit def tuplesWrites: Reads[Transaction] = Json.reads[Transaction]
}

