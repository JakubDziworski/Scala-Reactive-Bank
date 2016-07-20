package actors

import akka.actor.Status.Failure
import akka.actor.{ActorSystem, Props}
import exceptions.{BadVerificationCodeException, TransactionNotAwaitingVerificationException}
import models.{Transaction, TransactionVerification}
import models.dao.TransactionDao
import org.specs2.mutable._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeEach
import play.api.test.WithApplication
import akka.testkit._
import akka.actor.ActorDSL._
import akka.pattern.ask

import scala.concurrent.Future

/**
  * Created by kuba on 28.05.16.
  */

class TransactionActorTest extends TestKit(ActorSystem()) with SpecificationLike with Mockito {

//  sequential

//  "The actor" should {
//    val dao = mock[TransactionDao]
//    val transactinoActorRef = TestActorRef[TransactionActor](Props(new TransactionActor(dao)))
//    "start transaction when StartTransaction message received" in new WithApplication() {
//
//      implicit val i = inbox()
//      transactinoActorRef ! StartTransaction(Transaction("483275843", "493859435", "car payment", 3543))
//
//      i.receive() must beLike {
//        case TransactionVerification(1, _) => ok
//      }
//      there was one(dao).addTransaction(Transaction("483275843", "493859435", "car payment", 3543, Some(1)))
//      there was one(dao).addAwaitingVerification(like[TransactionVerification] { case TransactionVerification(1, _) => ok })
//      there were noMoreCallsTo(dao)
//    }
//  }
//  "The actor" should {
//    val dao = mock[TransactionDao]
//    val transactinoActorRef = TestActorRef[TransactionActor](Props(new TransactionActor(dao)))
//    "Verify transaction when VerifyTransaction message received" in new WithApplication() {
//
//      dao.getVerificationCodeForTransaction(25) returns Future[String]("9432")
//
//
//      implicit val i = inbox()
//      transactinoActorRef ! VerifyTransactionWithSms(TransactionVerification(25, "9432"))
//      i.receive() must beLike {
//        case _: String => ok
//      }
//
//      there was one(dao).getVerificationCodeForTransaction(25)
//      there was one(dao).markTransactionToCompleted(TransactionVerification(25, "9432"))
//      there were noMoreCallsTo(dao)
//    }
//  }
//  "The actor" should {
//    val dao = mock[TransactionDao]
//    val transactinoActorRef = TestActorRef[TransactionActor](Props(new TransactionActor(dao)))
//    "Fail To verify transaction when transaction is not awaiting verification" in new WithApplication() {
//
//      implicit val i = inbox()
//      transactinoActorRef ! VerifyTransactionWithSms(TransactionVerification(25, "9432"))
//      i.receive() must throwA [TransactionNotAwaitingVerificationException]
//
//      there were noMoreCallsTo(dao)
//    }
//  }
//
//  "The actor" should {
//    val dao = mock[TransactionDao]
//    val transactinoActorRef = TestActorRef[TransactionActor](Props(new TransactionActor(dao)))
//    "Fail (with BadVerificationCodeException) to verify transaction when sms codes do no match" in new WithApplication() {
//
//      dao.isAwaitingVerification(25) returns true
//      dao.getVerificationCodeForTransaction(25) returns Some("5211")
//
//
//      implicit val i = inbox()
//      transactinoActorRef ! VerifyTransactionWithSms(TransactionVerification(25, "9432"))
//      i.receive() must throwA [BadVerificationCodeException]
//
//      there was one(dao).isAwaitingVerification(25)
//      there was one(dao).getVerificationCodeForTransaction(25)
//      there were noMoreCallsTo(dao)
//    }
//  }
//
//  "The actor" should {
//    val dao = mock[TransactionDao]
//    val transactinoActorRef = TestActorRef[TransactionActor](Props(new TransactionActor(dao)))
//    "Fail (with TransactionNotAwaitingVerificationException) To cancel transaction when transaction is not awaiting verification" in new WithApplication() {
//
//      dao.isAwaitingVerification(25) returns false
//
//      implicit val i = inbox()
//      transactinoActorRef ! CancelTransaction(25)
//      i.receive() must throwA [TransactionNotAwaitingVerificationException]
//
//      there was one(dao).isAwaitingVerification(25)
//      there were noMoreCallsTo(dao)
//    }
//  }
//
//  "The actor" should {
//    val dao = mock[TransactionDao]
//    val transactinoActorRef = TestActorRef[TransactionActor](Props(new TransactionActor(dao)))
//    "Cancel transaction when CancelTransasction message received" in new WithApplication() {
//
//      dao.isAwaitingVerification(33) returns true
//
//
//      implicit val i = inbox()
//      transactinoActorRef ! CancelTransaction(33)
//      i.receive() must beLike {
//        case _: String => ok
//      }
//
//      there was one(dao).isAwaitingVerification(33)
//      there was one(dao).cancelTransaction(33)
//      there were noMoreCallsTo(dao)
//    }
//  }
}
