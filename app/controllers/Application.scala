package controllers

import io.swagger.annotations.{ApiOperation, Api}
import play.api.mvc.{Action, Controller}

import scala.annotation.meta.companionClass

/**
  * Created by kuba on 25.05.16.
  */
@Api("User Account")
object Application extends Controller {

  def test = Action {
    Ok("Reactive Bank - user account module - REACHABLE")
  }


}
