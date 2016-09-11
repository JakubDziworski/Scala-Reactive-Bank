package models.dao

import models.Account

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global



/**
  * Created by kuba on 11.09.16.
  */
class AccountSimpleDao extends AccountDao {

  val accounts = mutable.ListBuffer[Account]()

  def save(account: Account): Future[Account] = {
    val accountWithId = Account.create(Some(accounts.size + 1L), account)
    accounts += accountWithId
    Future.successful(accountWithId)
  }

  def findById(accountId: Long): Future[Option[Account]] = {
    Future(accounts.find(_.id.contains(accountId)))
  }

  def changeBalance(account: Account, newBalance: BigDecimal): Future[Unit] = {
    accounts -= account
    val newAccount = Account(account.owner, newBalance, account.id)
    accounts += newAccount
    Future.successful()
  }
}
