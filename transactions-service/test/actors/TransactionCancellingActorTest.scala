package actors

import akka.actor.ActorDSL._
import akka.actor.{ActorSystem, Props}
import akka.testkit._
import exceptions.TransactionNotAwaitingVerificationException
import models.dao.TransactionDao
import org.specs2.mock.Mockito
import org.specs2.mutable._
import play.api.test.WithApplication

/**
  * Created by kuba on 28.05.16.
  */

class TransactionCancellingActorTest extends TestKit(ActorSystem()) with SpecificationLike with Mockito {

  sequential
  "The actor" should {
    val dao = mock[TransactionDao]
    val transactinoActorRef = TestActorRef[TransactionCancellingActor](Props(new TransactionCancellingActor(dao)))
    "Fail (with TransactionNotAwaitingVerificationException) To cancel transaction when transaction is not awaiting verification" in new WithApplication() {

      dao.isAwaitingVerification(25) returns false

      implicit val i = inbox()
      transactinoActorRef ! CancelTransaction(25)
      i.receive() must throwA [TransactionNotAwaitingVerificationException]

      there was one(dao).isAwaitingVerification(25)
      there were noMoreCallsTo(dao)
    }
  }

  "The actor" should {
    val dao = mock[TransactionDao]
    val transactinoActorRef = TestActorRef[TransactionCancellingActor](Props(new TransactionCancellingActor(dao)))
    "Cancel transaction when CancelTransasction message received" in new WithApplication() {

      dao.isAwaitingVerification(33) returns true


      implicit val i = inbox()
      transactinoActorRef ! CancelTransaction(33)
      i.receive() must beLike {
        case _: String => ok
      }

      there was one(dao).isAwaitingVerification(33)
      there was one(dao).cancelTransaction(33)
      there were noMoreCallsTo(dao)
    }
  }
}
