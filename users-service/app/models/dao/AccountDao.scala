package models.dao

import com.typesafe.scalalogging.{LazyLogging, Logger}
import models.Account
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
  * Created by kuba on 26.05.16.
  */

class AccountDao extends LazyLogging {

  val accounts = mutable.ListBuffer[Account]()

  def save (account: Account): Account = {
    logger.info("adding account {}",account)
    val accountWithId = Account.create(Some(accounts.size + 1L), account)
    accounts += accountWithId
    accountWithId
  }

  def findById(accountId: Long):Option[Account] = {
    accounts.find(_.id.contains(accountId))
  }

  def changeBalance(account: Account, newBalance: BigDecimal):Unit  = {
    accounts -= account
    val newAccount = Account(account.owner,newBalance,account.id)
    accounts += newAccount
  }
}


