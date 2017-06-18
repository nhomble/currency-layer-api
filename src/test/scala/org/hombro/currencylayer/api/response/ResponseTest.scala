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
    val out = Response.parse("{\"success\":false,\"error\":{\"code\":-1}}").parseException()
    assert(out.code == -1)
    assert(out.message == "Unknown error code thrown")
  }

  test("parse exception from api call") {
    val out = Response.parse("{\"success\":false,\"error\":{\"code\":-1}}").currencyList()
    out match {
      case Success(_) => fail()
      case Failure(f) => succeed
    }
  }

  test("currencyList") {
    val json = "{" +
      "\"success\":true," +
      "\"terms\":\"https:\\/\\/currencylayer.com\\/terms\"," +
      "\"privacy\":\"https:\\/\\/currencylayer.com\\/privacy\"," +
      "\"currencies\":{" +
      " \"AED\":\"United Arab Emirates Dirham\"" +
      "}}"
    val out = Response.parse(json).currencyList()
    out match {
      case Success(o) =>
        assert(o.currencies.length == 1)
        assert(o.currencies.head.enumeration == "AED")
        assert(o.currencies.head.description == "United Arab Emirates Dirham")
      case Failure(_) => fail()
    }
  }

  test("quoteQuery") {
    val json = "{" +
      "\"success\":true," +
      "\"terms\":\"https:\\/\\/currencylayer.com\\/terms\"," +
      "\"privacy\":\"https:\\/\\/currencylayer.com\\/privacy\"," +
      "\"timestamp\":1497744547," +
      "\"source\":\"USD\"," +
      "\"quotes\":{" +
      " \"USDEUR\":0.5" +
      "}}"
    val out = Response.parse(json).quoteQuery()
    out match {
      case Success(o) =>
        assert(o.source == "USD")
        assert(o.quotes.head.currency == "USDEUR")
        assert(o.quotes.head.rate == .5)
      case Failure(_) => fail()
    }
  }

  test("historicQuoteQuery") {
    val json = "{" +
      "\"success\":true," +
      "\"terms\":\"https:\\/\\/currencylayer.com\\/terms\"," +
      "\"privacy\":\"https:\\/\\/currencylayer.com\\/privacy\"," +
      "\"historical\":true," +
      "\"date\":\"2010-06-18\"," +
      "\"timestamp\":1276905599,\"" +
      "source\":\"USD\"," +
      "\"quotes\":{" +
      " \"USDUSD\":1" +
      "}}"
    val out = Response.parse(json).historicQuoteQuery()
    out match {
      case Success(o) =>
        assert(o.date == CurrencyLayerClient.DATE_FORMATTER.parse("2010-06-18"))
        assert(o.source == "USD")
        assert(o.quotes.head.currency == "USDUSD")
        assert(o.quotes.head.rate == 1)
    }
  }
}
