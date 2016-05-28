package models

import play.api.libs.json.Json

/**
  * Created by kuba on 27.05.16.
  */
case class TransactionVerification(transactionId:Long, smscode : String)
object TransactionVerification {
  implicit val writes = Json.writes[TransactionVerification]
  implicit val reads = Json.reads[TransactionVerification]
}
