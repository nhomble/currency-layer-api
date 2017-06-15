# currency-layer-api
scala client for https://currencylayer.com/


```scala
package org.hombro.currencylayer

import org.joda.time.DateTime

import scala.util.{Failure, Success}

object Driver {
  def main(args: Array[String]): Unit = {
    val apiKey = "YOUR API KEY - NOT MINE"
    val client = CurrencyLayerClient.client(apiKey)
    client.historicalRate(DateTime.now.toDate) match {
      case Failure(e) => throw e
      case Success(out) =>
        val quote = out.quotes.head
        print(s"Response: ${quote.currency} ${quote.rate}")

    }
  }
}
```