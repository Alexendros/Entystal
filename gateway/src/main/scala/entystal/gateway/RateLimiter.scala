package entystal.gateway

import scala.collection.concurrent.TrieMap

/** Control de tasa simple tipo token bucket por IP */
object RateLimiter {
  private case class Bucket(tokens: Int, last: Long)
  private val capacity = 20
  private val refillMs = 60000L
  private val buckets  = TrieMap.empty[String, Bucket]

  def allow(ip: String): Boolean = synchronized {
    val now    = System.currentTimeMillis()
    val bucket = buckets.getOrElse(ip, Bucket(capacity, now))
    val fresh  =
      if (now - bucket.last >= refillMs) Bucket(capacity - 1, now)
      else if (bucket.tokens > 0) bucket.copy(tokens = bucket.tokens - 1)
      else bucket
    buckets.update(ip, fresh)
    fresh.tokens >= 0
  }
}
