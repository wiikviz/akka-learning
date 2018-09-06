package ru.sber.cb.ap.domain

import scala.collection.immutable.HashMap

object BusinessLogicDomain {

  type FirstName = String
  type LastName = String

  type Balance = Double
  type AccountDirectory = HashMap[LastName, Account]

  val defaultBalance: Balance = 0.0
  val emptyDirectory: AccountDirectory = HashMap.empty[LastName, Account]

  def addAccount(newAccount: Account, accountDir: AccountDirectory): AccountDirectory =
    if (accountDir contains newAccount.lastName)
      accountDir
    else
      accountDir + (newAccount.lastName -> newAccount)

  case class Account(firstName: FirstName,
                     lastName: LastName,
                     balance: Balance = defaultBalance)

}
