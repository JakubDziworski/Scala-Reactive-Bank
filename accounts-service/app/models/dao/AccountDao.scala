package models.dao

import models.Account

import scala.concurrent.Future

/**
  * Created by kuba on 26.05.16.
  */

trait AccountDao  {
  def save(account: Account): Future[Account]
  def findById(accountId: Long): Future[Option[Account]]
  def changeBalance(account: Account, newBalance: BigDecimal): Future[Unit]
}


