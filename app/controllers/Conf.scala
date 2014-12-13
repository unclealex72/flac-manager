package controllers

import play.api.Configuration
import play.api.mvc.{Action, Controller}

/**
 * Created by alex on 13/12/14.
 */
class Conf(val conf: Configuration) extends Controller{

  def configuration = Action { implicit request =>
    Ok(conf.underlying.root().render())
  }
}
