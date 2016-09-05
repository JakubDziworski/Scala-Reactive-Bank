package actors

import akka.actor.ActorDSL._
import akka.actor.{ActorSystem, Props}
import akka.testkit._
import models.dao.TransactionDao
import models.{Transaction, TransactionVerification}
import org.specs2.mock.Mockito
import org.specs2.mutable._
import play.api.test.WithApplication

/**
  * Created by kuba on 28.05.16.
  */

class TransactionStartingActorTest extends TestKit(ActorSystem()) with SpecificationLike with Mockito {

  sequential
  "The actor" should {
    val dao = mock[TransactionDao]
    val transactinoActorRef = TestActorRef[TransactionStartingActor](Props(new TransactionStartingActor(dao)))
    "start transaction when StartTransaction message received" in new WithApplication() {

      dao.getNextAvailableTransactionId returns 1

      implicit val i = inbox()
      transactinoActorRef ! StartTransaction(Transaction(483275843, 493859435, "car payment", 3543, None))

      i.receive() must beLike {
        case TransactionVerification(1, _) => ok
      }
      there was one(dao).getNextAvailableTransactionId
      there was one(dao).addTransaction(Transaction(483275843, 493859435, "car payment", 3543, Some(1)))
      there was one(dao).addAwaitingVerification(like[TransactionVerification] { case TransactionVerification(1, _) => ok })
      there were noMoreCallsTo(dao)
    }
  }
}
