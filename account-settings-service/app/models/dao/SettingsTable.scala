package models.dao

import models.domain.Setting
import slick.driver.PostgresDriver.api._
import slick.lifted.{Index, ProvenShape}
/**
  * Created by kuba on 20.07.16.
  */
case class SettingsTable(tag: Tag) extends Table[Setting](tag, "SETTINGS") {

  def accountId: Rep[Long] = column[Long]("ACCOUNT_ID",O.PrimaryKey)
  def idxAccountId: Index = index("idx_account_id", accountId, unique = true)

  def transactionValueLimit: Rep[Option[BigDecimal]] = column[Option[BigDecimal]]("TRANSACTION_LIMIT")

  override def * : ProvenShape[Setting] = (accountId, transactionValueLimit) <>((Setting.apply _).tupled, Setting.unapply)
}
