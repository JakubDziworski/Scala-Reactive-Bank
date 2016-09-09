package actors

import actors.messages.{StartTransaction, TransactionStarted}
import akka.actor.ActorDSL._
import akka.actor.{ActorSystem, Props}
import akka.testkit._
import models.dao.TransactionDao
import models.{Transaction, TransactionSmsCode}
import org.specs2.mock.Mockito
import org.specs2.mutable._
import play.api.test.WithApplication

import scala.concurrent.Future

/**
  * Created by kuba on 28.05.16.
  */

class TransactionStartingActorTest extends TestKit(ActorSystem()) with SpecificationLike with Mockito {

  sequential
  "The actor" should {
    val dao = mock[TransactionDao]
    val transactinoActorRef = TestActorRef[TransactionStartingActor](Props(new TransactionStartingActor(dao)))
    "start transaction when StartTransaction message received" in new WithApplication() {

      val transaction = Transaction(483275843, 493859435, "car payment", 3543)
      dao.addTransaction(transaction) returns Future.successful("332")

      implicit val i = inbox()
      transactinoActorRef ! StartTransaction(transaction)

      i.receive() must beLike {
        case TransactionStarted(TransactionSmsCode("332", _)) => ok
      }
      there was one(dao).addTransaction(transaction)
      there was one(dao).addTransactionSmsCode(like[TransactionSmsCode] { case TransactionSmsCode("332", _) => ok })
      there were noMoreCallsTo(dao)
    }
  }
}
