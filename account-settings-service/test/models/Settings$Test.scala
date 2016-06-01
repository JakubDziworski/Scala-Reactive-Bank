package models

import org.specs2.mutable.Specification

/**
  * Created by kuba on 01.06.16.
  */
class Settings$Test extends Specification {

  "Settings" should {
    "be merged with all new settings values" in {
      val oldSettings = Settings(1,Some(255),Some(3))
      val newSettings = Settings(1,Some(334),Some(1))
      val merged = Settings.merge(oldSettings, newSettings)
      merged == newSettings
    }
  }

  "Settings" should {
    "be merged with transactions per day only" in {
      val oldSettings = Settings(1,Some(255),Some(3))
      val newSettings = Settings(1,None,Some(1))
      val merged = Settings.merge(oldSettings, newSettings)
      merged == Settings(1,Some(255),Some(1))
    }
  }
}
