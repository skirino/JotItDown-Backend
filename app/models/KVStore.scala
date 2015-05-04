package models

import scala.collection.mutable
import org.sedis._
import redis.clients.jedis._

object KVStore {
  import collection.JavaConverters._

  lazy val pool = {
    val uri = new java.net.URI(System.getenv("REDISCLOUD_URL"))
    new Pool(new JedisPool(new JedisPoolConfig,
                           uri.getHost,
                           uri.getPort,
                           Protocol.DEFAULT_TIMEOUT,
                           uri.getUserInfo.split(":", 2)(1)))
  }

  def itemKey(id: String, key: String) = id + '/' + key

  def credentialKey(cred: String) = "credential/" + cred

  def keysOf(id: String, client: Dress.Wrap): List[String] = {
    client.keys(itemKey(id, "*")).asScala.toList.map(_.substring(id.size + 1))
  }

  def kvPairsFromKeys(id: String, client: Dress.Wrap, keys: Array[String]): Map[String, String] = {
    val keysWithPrefix = keys.map { itemKey(id, _) }
    val vals = client.mget(keysWithPrefix: _*).asScala
    (keys, vals).zipped.toMap
  }

  def storeKVPairs(id: String, client: Dress.Wrap, kvs: Map[String, String]) {
    if(kvs.isEmpty) return
    val kvArray = new mutable.ArrayBuffer[String]
    kvs.foreach { case (k, v) =>
      kvArray += itemKey(id, k)
      kvArray += v
    }
    client.mset(kvArray: _*)
  }

  def deleteKeys(id: String, client: Dress.Wrap, keys: Array[String]) {
    val keysWithPrefix = keys.map { itemKey(id, _) }
    client.del(keysWithPrefix: _*)
  }
}
