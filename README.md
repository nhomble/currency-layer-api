# currency-layer-api
scala client for https://currencylayer.com/

[![Build Status](https://travis-ci.org/nhomble/currency-layer-api.svg?branch=master)](https://travis-ci.org/nhomble/currency-layer-api)
[![codecov](https://codecov.io/gh/nhomble/currency-layer-api/branch/master/graph/badge.svg)](https://codecov.io/gh/nhomble/currency-layer-api)
```scala
package org.hombro.currencylayer

import scala.util.{Failure, Success}

object Driver {
  def main(args: Array[String]): Unit = {
    val apiKey = "YOUR API KEY - NOT MINE"
    val client = CurrencyLayerClient.client(apiKey)
    client.liveRate() match {
      case Failure(e) => throw e
      case Success(out) =>
        val quote = out.quotes.head
        print(s"Response: ${quote.currency} ${quote.rate}")
    }
  }
}
```