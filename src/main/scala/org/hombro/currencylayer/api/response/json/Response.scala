package org.hombro.currencylayer.api.response.json

import org.hombro.currencylayer.api.response.error.CurrencyLayerError
import org.joda.time.DateTime

import scala.util.{Failure, Success}
import scala.util.parsing.json.JSON

object Response {
  def parse(json: String) = {
    val parsed = JSON.parseFull(json)
    parsed match {
      case Some(map: Map[String, Any]) =>
        val isSuccess = map.get("success").get.asInstanceOf[Boolean]
        Response(isSuccess, map)
      case None => throw new RuntimeException("failed to parsed the json")
      case _ => throw new RuntimeException("unrecognized map structure")
    }
  }

  def apply(isSuccess: Boolean, payload: Map[String, Any]) = new Response(isSuccess, payload)
}

/**
  * Created by nicolas on 6/12/2017.
  */
class Response(isSuccess: Boolean, payload: Map[String, Any]) {
  def parseException() = {
    val code = payload.get("error").get.asInstanceOf[Map[String, Double]].get("code").get.asInstanceOf[Int]
    CurrencyLayerError.getException(code)
  }

  def currencyList() = {
    if (!isSuccess)
      Failure(parseException())
    else {
      val parsed = payload.get("currencies").get.asInstanceOf[Map[String, String]]
      val currencies = parsed.keys.map(s => Currency(s, parsed.get(s).get)).toList
      Success(CurrencyList(currencies))
    }
  }

  def quoteQuery() = {
    if (!isSuccess)
      Failure(parseException())
    else {
      val timestamp = payload.get("timestamp").get.asInstanceOf[Double]
      val source = payload.get("source").get.asInstanceOf[String]
      val quotesMap = payload.get("quotes").get.asInstanceOf[Map[String, Double]]
      val quotes = quotesMap.keys.map(s => Quote(s, quotesMap.get(s).get)).toList
      Success(InstantQuoteQuery(timestamp, source, quotes))
    }
  }

  def historicQuoteQuery() = {
    if (!isSuccess)
      Failure(parseException())
    else {
      quoteQuery() match {
        case Failure(e) => Failure(e)
        case Success(query) =>
          val date = DateTime.parse(payload.get("date").get.asInstanceOf[String]).toDate
          Success(HistoricQuoteQuery(date, query.timestamp, query.source, query.quotes))
      }
    }
  }
}
