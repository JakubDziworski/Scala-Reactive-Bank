package actors

import akka.actor.ActorDSL._
import akka.actor.{ActorSystem, Props}
import akka.testkit._
import exceptions.{BadVerificationCodeException, TransactionNotAwaitingVerificationException}
import models.TransactionVerification
import models.dao.TransactionDao
import org.specs2.mock.Mockito
import org.specs2.mutable._
import play.api.test.WithApplication

/**
  * Created by kuba on 28.05.16.
  */

class TransactionSmsVerificationActorTest extends TestKit(ActorSystem()) with SpecificationLike with Mockito {

  sequential
  "The actor" should {
    val dao = mock[TransactionDao]
    val transactinoActorRef = TestActorRef[TransactionSmsVerifierActor](Props(new TransactionSmsVerifierActor(dao)))
    "Verify transaction when VerifyTransaction message received" in new WithApplication() {

      dao.isAwaitingVerification(25) returns true
      dao.getVerificationCodeForTransaction(25) returns Some("9432")


      implicit val i = inbox()
      transactinoActorRef ! VerifyTransactionWithSms(TransactionVerification(25, "9432"))
      i.receive() must beLike {
        case _: String => ok
      }

      there was one(dao).isAwaitingVerification(25)
      there was one(dao).getVerificationCodeForTransaction(25)
      there was one(dao).markTransactionToCompleted(TransactionVerification(25, "9432"))
      there were noMoreCallsTo(dao)
    }
  }
  "The actor" should {
    val dao = mock[TransactionDao]
    val transactinoActorRef = TestActorRef[TransactionSmsVerifierActor](Props(new TransactionSmsVerifierActor(dao)))
    "Fail To verify transaction when transaction is not awaiting verification" in new WithApplication() {

      dao.isAwaitingVerification(25) returns false

      implicit val i = inbox()
      transactinoActorRef ! VerifyTransactionWithSms(TransactionVerification(25, "9432"))
      i.receive() must throwA [TransactionNotAwaitingVerificationException]

      there was one(dao).isAwaitingVerification(25)
      there were noMoreCallsTo(dao)
    }
  }

  "The actor" should {
    val dao = mock[TransactionDao]
    val transactinoActorRef = TestActorRef[TransactionSmsVerifierActor](Props(new TransactionSmsVerifierActor(dao)))
    "Fail (with BadVerificationCodeException) to verify transaction when sms codes do no match" in new WithApplication() {

      dao.isAwaitingVerification(25) returns true
      dao.getVerificationCodeForTransaction(25) returns Some("5211")


      implicit val i = inbox()
      transactinoActorRef ! VerifyTransactionWithSms(TransactionVerification(25, "9432"))
      i.receive() must throwA [BadVerificationCodeException]

      there was one(dao).isAwaitingVerification(25)
      there was one(dao).getVerificationCodeForTransaction(25)
      there were noMoreCallsTo(dao)
    }
  }
}
