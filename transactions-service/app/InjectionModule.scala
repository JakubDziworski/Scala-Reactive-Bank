/**
  * Created by kuba on 08.09.16.
  */
import com.google.inject.{AbstractModule, Singleton}
import models.dao.TransactionDao
import models.dao.mongodb.TransactionMongoDao

class InjectionModule extends AbstractModule {
  def configure(): Unit = {
    bind(classOf[TransactionDao]).to(classOf[TransactionMongoDao]).in(classOf[Singleton])
  }
}