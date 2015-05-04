package controllers

import scala.collection.JavaConversions._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import models.KVStore._

object Application extends Controller
{
  def index = Action {
    Ok("I'm ready!")
  }

  def register = Action { request =>
    val params = request.body.asFormUrlEncoded.getOrElse(Map())
    val id = params("username").head
    val pw = params("password").head
    val cred = AuthHelper.genCredential(id, pw)
    pool.withClient { client =>
      client.set(credentialKey(cred), id)
      Ok(cred)
    }
  }
}
