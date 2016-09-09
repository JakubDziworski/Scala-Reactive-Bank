package models

import play.api.libs.json.Json

/**
  * Created by kuba on 27.05.16.
  */
case class TransactionSmsCode(transactionId:String, smscode : String)
object TransactionSmsCode {
  implicit val writes = Json.writes[TransactionSmsCode]
  implicit val reads = Json.reads[TransactionSmsCode]
}
