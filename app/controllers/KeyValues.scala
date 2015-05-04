package controllers

import scala.collection.JavaConversions._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import org.sedis._
import models.KVStore._

object KeyValues extends Controller {
  def index() = Action { request =>
    withValidAuth(request) { (id, client) =>
      Ok(Json.toJson(keysOf(id, client)))
    }
  }

  def delete(keys: String) = Action { request =>
    withValidAuth(request) { (id, client) =>
      deleteKeys(id, client, keys.split(','))
      Ok("delete: Success")
    }
  }

  def get(keys: String) = Action { request =>
    withValidAuth(request) { (id, client) =>
      val kvPairs = kvPairsFromKeys(id, client, keys.split(','))
      Ok(Json.toJson(kvPairs))
    }
  }

  def set() = Action(parse.json) { request =>
    withValidAuth(request) { (id, client) =>
      request.body.asOpt[Map[String, String]] match {
        case Some(kvs) =>
          storeKVPairs(id, client, kvs)
          Ok("set: Success.")
        case None => BadRequest("Unexpected parameters!")
      }
    }
  }

  private def withValidAuth[REQ, RES](request: Request[REQ]) (f: (String, Dress.Wrap) => Result) = {
    request.headers.get("Authorization") match {
      case Some(cred) => withValidCredentialStored(cred, f)
      case None       => Unauthorized
    }
  }

  private def withValidCredentialStored[RES](cred: String, f: (String, Dress.Wrap) => Result) = {
    pool.withClient { client =>
      client.get(credentialKey(cred)) match {
        case Some(id) => f(id, client)
        case None     => Unauthorized
      }
    }
  }
}
