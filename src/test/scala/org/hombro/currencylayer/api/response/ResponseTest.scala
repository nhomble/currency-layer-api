package org.hombro.currencylayer.api.response

import org.hombro.currencylayer.CurrencyLayerClient
import org.hombro.currencylayer.api.response.json.Response
import org.scalatest.FunSuite

import scala.util.{Failure, Success}

/**
  * Created by nicolas on 6/17/2017.
  */
class ResponseTest extends FunSuite {
  test("empty json response will throw runtime exception") {
    assertThrows[RuntimeException] {
      Response.parse("")
    }
  }
  test("invalid json will throw runtime exception") {
    assertThrows[RuntimeException] {
      Response.parse("{")
    }
  }

  test("parse exception") {
    val out = Response.parse(
      """
        |{
        | "success": false,
        | "error": {
        |   "code": -1
        | }
        |}
      """.stripMargin).parseException()
    assert(out.code == -1)
    assert(out.message == "Unknown error code thrown")
  }

  test("parse exception from api call") {
    val out = Response.parse(
      """
        |{
        | "success": false,
        | "error": {
        |   "code": -1
        | }
        |}
      """.stripMargin).currencyList()
    out match {
      case Success(_) => fail()
      case Failure(f) => succeed
    }
  }

  test("currencyList") {
    val json =
      """
        |{
        | "success": true,
        |  "currencies": {
        |    "AED": "United Arab Emirates Dirham"
        |    }
        |} """.stripMargin
    val out = Response.parse(json).currencyList()
    out match {
      case Success(o) =>
        assert(o.currencies.length == 1)
        assert(o.currencies.head.enumeration == "AED")
        assert(o.currencies.head.description == "United Arab Emirates Dirham")
      case Failure(o) => fail()
    }
  }

  test("quoteQuery") {
    val json =
      """
        |{
        | "success": true,
        |  "terms": "https://currencylayer.com/terms",
        |  "privacy": "https://currencylayer.com/privacy",
        |    "timestamp": 1430401802,
        |    "source": "USD",
        |    "quotes": {
        |        "USDAED": 3.672982
        |    }
        |}
      """.stripMargin
    val out = Response.parse(json).quoteQuery()
    out match {
      case Success(o) =>
        assert(o.source == "USD")
        assert(o.quotes.head.currency == "USDAED")
        assert(o.quotes.head.rate == 3.672982)
      case Failure(o) => fail()
    }
  }

  test("historicQuoteQuery") {
    val json =
      """
        |{
        |  "success": true,
        |  "terms": "https://currencylayer.com/terms",
        |  "privacy": "https://currencylayer.com/privacy",
        |  "historical": true,
        |  "date": "2005-02-01",
        |  "timestamp": 1107302399,
        |  "source": "USD",
        |  "quotes": {
        |    "USDAED": 3.67266
        |  }
        |}
      """.stripMargin
    val out = Response.parse(json).historicQuoteQuery()
    out match {
      case Success(o) =>
        assert(o.date == CurrencyLayerClient.DATE_FORMATTER.parse("2005-02-01"))
        assert(o.source == "USD")
        assert(o.quotes.head.currency == "USDAED")
        assert(o.quotes.head.rate == 3.67266)
      case Failure(o) => fail()
    }
  }

  test("convert") {
    val json =
      """
        |{
        |  "success": true,
        |  "terms": "https://currencylayer.com/terms",
        |  "privacy": "https://currencylayer.com/privacy",
        |  "query": {
        |    "from": "USD",
        |    "to": "GBP",
        |    "amount": 10
        |  },
        |  "info": {
        |    "timestamp": 1430068515,
        |    "quote": 0.658443
        |  },
        |  "result": 6.58443
        |}
      """.stripMargin
    val out = Response.parse(json).convert()
    out match {
      case Success(o) =>
        assert(o.result == 6.58443)
        assert(o.info.timestamp == 1430068515)
        assert(o.info.quote == 0.658443)
        assert(o.query.from == "USD")
        assert(o.query.to == "GBP")
        assert(o.query.amount == 10)
      case Failure(o) => fail()
    }
  }

  test("historic conversion") {
    val json =
      """
        |{
        |  "success": true,
        |  "terms": "https://currencylayer.com/terms",
        |  "privacy": "https://currencylayer.com/privacy",
        |  "query": {
        |    "from": "USD",
        |    "to": "GBP",
        |    "amount": 10
        |  },
        |  "info": {
        |    "timestamp": 1104623999,
        |    "quote": 0.51961
        |  },
        |  "historical": true,
        |  "date": "2005-01-01",
        |  "result": 5.1961
        |}
      """.stripMargin
    val out = Response.parse(json).historicConvert()
    out match {
      case Success(o) =>
        assert(o.result == 5.1961)
        assert(o.info.timestamp == 1104623999)
        assert(o.info.quote == 0.51961)
        assert(o.query.from == "USD")
        assert(o.query.to == "GBP")
        assert(o.query.amount == 10)
        assert(o.date == CurrencyLayerClient.DATE_FORMATTER.parse("2005-01-01"))
      case Failure(o) => fail()
    }
  }

  test("rates over interval") {
    val json =
      """
        |{
        |  "success": true,
        |  "terms": "https://currencylayer.com/terms",
        |  "privacy": "https://currencylayer.com/privacy",
        |  "timeframe": true,
        |  "start_date": "2010-03-01",
        |  "end_date": "2010-04-01",
        |  "source": "USD",
        |  "quotes": {
        |    "2010-03-01": {
        |      "USDUSD": 1,
        |      "USDGBP": 0.668525,
        |      "USDEUR": 0.738541
        |    },
        |    "2010-03-02": {
        |      "USDUSD": 1,
        |      "USDGBP": 0.668827,
        |      "USDEUR": 0.736145
        |    }
        |  }
        |}
      """.stripMargin
    val out = Response.parse(json).ratesOverInterval()
    out match {
      case Failure(_) => fail()
      case Success(o) =>
        assert(o.rates.size == 2)
        val rates = o.rates.get(CurrencyLayerClient.DATE_FORMATTER.parse("2010-03-02")).get
        assert(rates.size == 3)
    }
  }

  test("change in currency") {
    val json =
      """
        |{
        |  "success": true,
        |  "terms": "https://currencylayer.com/terms",
        |  "privacy": "https://currencylayer.com/privacy",
        |  "change": true,
        |  "start_date": "2005-01-01",
        |  "end_date": "2010-01-01",
        |  "source": "USD",
        |  "quotes": {
        |    "USDAUD": {
        |      "start_rate": 1.281236,
        |      "end_rate": 1.108609,
        |      "change": -0.1726,
        |      "change_pct": -13.4735
        |    },
        |    "USDEUR": {
        |      "start_rate": 0.73618,
        |      "end_rate": 0.697253,
        |      "change": -0.0389,
        |      "change_pct": -5.2877
        |    },
        |    "USDMXN":{
        |      "start_rate": 11.149362,
        |      "end_rate": 13.108757,
        |      "change": 1.9594,
        |      "change_pct": 17.5741
        |    }
        |  }
        |}
      """.stripMargin
    val out = Response.parse(json).currencyChangeOverInterval()
    out match {
      case Failure(_) => fail()
      case Success(o) =>
        assert(o.size == 3)
        assert(o.exists(cc => {
          cc.startDate == CurrencyLayerClient.DATE_FORMATTER.parse("2005-01-01") &&
            cc.endDate == CurrencyLayerClient.DATE_FORMATTER.parse("2010-01-01") &&
            cc.source == "USD" &&
            cc.pair == "USDAUD" &&
            cc.startRate == 1.281236 &&
            cc.endRate == 1.108609 &&
            cc.change == -0.1726 &&
            cc.changePercentage == -13.4735
        }))
    }
  }
}
