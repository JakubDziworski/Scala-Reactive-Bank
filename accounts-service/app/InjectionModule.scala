/**
  * Created by kuba on 11.09.16.
  */
/**
  * Created by kuba on 08.09.16.
  */
import com.google.inject.{AbstractModule, Singleton}
import models.dao.{AccountDao, AccountSimpleDao}

class InjectionModule extends AbstractModule {
  def configure(): Unit = {
    bind(classOf[AccountDao]).to(classOf[AccountSimpleDao]).in(classOf[Singleton])
  }
}