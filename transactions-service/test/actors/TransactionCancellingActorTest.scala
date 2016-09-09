package actors

import actors.messages.{CancelTransaction, TransactionCancelled}
import akka.actor.ActorDSL._
import akka.actor.{ActorSystem, Props}
import akka.testkit._
import exceptions.TransactionNotAwaitingVerificationException
import models.dao.TransactionDao
import org.specs2.mock.Mockito
import org.specs2.mutable._
import play.api.test.WithApplication

import scala.concurrent.Future

/**
  * Created by kuba on 28.05.16.
  */

class TransactionCancellingActorTest extends TestKit(ActorSystem()) with SpecificationLike with Mockito {

  sequential
  "The actor" should {
    val dao = mock[TransactionDao]
    val transactinoActorRef = TestActorRef[TransactionCancellingActor](Props(new TransactionCancellingActor(dao)))
    "Fail (with TransactionNotAwaitingVerificationException) To cancel transaction when transaction is not awaiting verification" in new WithApplication() {

      dao.setTransactionCancelled("25") returns Future.failed(TransactionNotAwaitingVerificationException("25"))

      implicit val i = inbox()
      transactinoActorRef ! CancelTransaction("25")
      i.receive() must throwA [TransactionNotAwaitingVerificationException]

      there was one(dao).setTransactionCancelled("25")
      there were noMoreCallsTo(dao)
    }
  }

  "The actor" should {
    val dao = mock[TransactionDao]
    val transactinoActorRef = TestActorRef[TransactionCancellingActor](Props(new TransactionCancellingActor(dao)))
    "Cancel transaction when CancelTransasction message received" in new WithApplication() {

      dao.setTransactionCancelled("33") returns Future.successful()


      implicit val i = inbox()
      transactinoActorRef ! CancelTransaction("33")
      i.receive() must beLike {
        case _: TransactionCancelled => ok
      }

      there was one(dao).setTransactionCancelled("33")
      there were noMoreCallsTo(dao)
    }
  }
}
