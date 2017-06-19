package org.hombro.currencylayer.api.response.json

import org.hombro.currencylayer.api.response.error.CurrencyLayerError
import org.hombro.currencylayer.api.response.json.model._
import org.joda.time.DateTime

import scala.util.parsing.json.JSON
import scala.util.{Failure, Success}

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
      val currencies = parsed.keys.map(s => new Currency(s, parsed.get(s).get)).toList
      Success(CurrencyList(currencies))
    }
  }

  def quoteQuery() = {
    if (!isSuccess)
      Failure(parseException())
    else {
      val timestamp = payload.get("timestamp").get.asInstanceOf[Double].toLong
      val source = payload.get("source").get.asInstanceOf[String]
      val quotesMap = payload.get("quotes").get.asInstanceOf[Map[String, Double]]
      val quotes = quotesMap.keys.map(s => new Quote(s, quotesMap.get(s).get)).toList
      Success(LiveQuoteQuery(timestamp, source, quotes))
    }
  }

  def historicQuoteQuery() = {
    quoteQuery() match {
      case Success(q) =>
        val date = DateTime.parse(payload.get("date").get.asInstanceOf[String]).toDate
        Success(HistoricQuoteQuery(date, q.timestamp, q.source, q.quotes))
      case Failure(f) => Failure(f)
    }
  }

  def convert() = {
    if (!isSuccess)
      Failure(parseException())
    else {
      val query = payload.get("query").get.asInstanceOf[Map[String, Any]]
      val info = payload.get("info").get.asInstanceOf[Map[String, Any]]
      val result = payload.get("result").get.asInstanceOf[Double]

      val q = new ConvertQuery(
        query.get("from").get.asInstanceOf[String],
        query.get("to").get.asInstanceOf[String],
        query.get("amount").get.asInstanceOf[Double].toLong
      )
      val i = new ConvertInfo(
        info.get("timestamp").get.asInstanceOf[Double].toLong,
        info.get("quote").get.asInstanceOf[Double]
      )
      Success(LiveConvert(q, i, result))
    }
  }

  def historicConvert() = {
    convert() match {
      case Success(q) =>
        val date = DateTime.parse(payload.get("date").get.asInstanceOf[String]).toDate
        Success(HistoricConvert(date, q.query, q.info, q.result))
      case Failure(f) => Failure(f)
    }
  }
}
