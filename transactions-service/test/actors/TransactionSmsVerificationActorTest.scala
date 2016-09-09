package actors

import actors.messages.{TransactionVerified, VerifyTransactionWithSms}
import akka.actor.ActorDSL._
import akka.actor.{ActorSystem, Props}
import akka.testkit._
import exceptions.{BadVerificationCodeException, TransactionNotAwaitingVerificationException}
import models.TransactionSmsCode
import models.dao.TransactionDao
import org.specs2.mock.Mockito
import org.specs2.mutable._
import play.api.test.WithApplication

import scala.concurrent.Future

/**
  * Created by kuba on 28.05.16.
  */

class TransactionSmsVerificationActorTest extends TestKit(ActorSystem()) with SpecificationLike with Mockito {

  sequential
  "The actor" should {
    val dao = mock[TransactionDao]
    val transactinoActorRef = TestActorRef[TransactionSmsVerifierActor](Props(new TransactionSmsVerifierActor(dao)))
    "Verify transaction when VerifyTransaction message received" in new WithApplication() {

      dao.getSmsCodeForTransaction("25") returns Future.successful("9432")
      dao.setTransactionVerified("25") returns Future.successful()


      implicit val i = inbox()
      transactinoActorRef ! VerifyTransactionWithSms(TransactionSmsCode("25", "9432"))
      i.receive() must beLike {
        case _: TransactionVerified => ok
      }

      there was one(dao).getSmsCodeForTransaction("25")
      there was one(dao).setTransactionVerified("25")
      there were noMoreCallsTo(dao)
    }
  }
  "The actor" should {
    val dao = mock[TransactionDao]
    val transactinoActorRef = TestActorRef[TransactionSmsVerifierActor](Props(new TransactionSmsVerifierActor(dao)))
    "Fail To verify transaction when transaction is not awaiting verification" in new WithApplication() {

      dao.getSmsCodeForTransaction("25") returns Future.failed(TransactionNotAwaitingVerificationException("25"))

      implicit val i = inbox()
      transactinoActorRef ! VerifyTransactionWithSms(TransactionSmsCode("25", "9432"))
      i.receive() must throwA [TransactionNotAwaitingVerificationException]

      there was one(dao).getSmsCodeForTransaction("25")
      there were noMoreCallsTo(dao)
    }
  }

  "The actor" should {
    val dao = mock[TransactionDao]
    val transactinoActorRef = TestActorRef[TransactionSmsVerifierActor](Props(new TransactionSmsVerifierActor(dao)))
    "Fail (with BadVerificationCodeException) to verify transaction when sms codes do no match" in new WithApplication() {

      dao.getSmsCodeForTransaction("25") returns Future.successful("5211")


      implicit val i = inbox()
      transactinoActorRef ! VerifyTransactionWithSms(TransactionSmsCode("25", "9432"))
      i.receive() must throwA [BadVerificationCodeException]

      there was one(dao).getSmsCodeForTransaction("25")
      there were noMoreCallsTo(dao)
    }
  }
}
