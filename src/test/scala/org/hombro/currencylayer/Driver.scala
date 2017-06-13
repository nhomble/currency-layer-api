package org.hombro.currencylayer

import org.hombro.currencylayer.api.client.SynchronousClient
import org.joda.time.DateTime

/**
  * Created by nicolas on 6/12/2017.
  */
object Driver {
  def main(args: Array[String]): Unit = {
    val apiKey = "api key"
    val client = new SynchronousClient(apiKey)
    val out = client.historicalRate(DateTime.now.toDate) match {
      case Right(query) => query
      case Left(exception) => throw exception
    }
    print(out)
  }
}
