package models.dao

import models.domain.Setting
import slick.driver.PostgresDriver.api._
/**
  * Created by kuba on 20.07.16.
  */
case class SettingsTable(tag: Tag) extends Table[Setting](tag, "SETTINGS") {

  def accountId = column[Long]("ACCOUNT_ID",O.PrimaryKey)
  def idxAccountId = index("idx_account_id", accountId, unique = true)

  def transactionValueLimit = column[Option[BigDecimal]]("TRANSACTION_LIMIT")

  override def * = (accountId, transactionValueLimit) <>((Setting.apply _).tupled, Setting.unapply)
}