package org.hombro.currencylayer.api.response.json

import org.hombro.currencylayer.CurrencyLayerClient
import org.hombro.currencylayer.api.response.error.CurrencyLayerError
import org.hombro.currencylayer.api.response.json.model._

import scala.util.parsing.json.JSON
import scala.util.{Failure, Success}

object Response {
  def parse(json: String) = {
    val parsed = JSON.parseFull(json)
    parsed match {
      case Some(map) =>
        val casted = map.asInstanceOf[Map[String, Any]]
        val isSuccess = casted.get("success").get.asInstanceOf[Boolean]
        Response(isSuccess, casted)
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
  // TODO still come duplication to cover, probably should move this logic into the models since they should know how to
  // create themselves from the json

  // TODO should default to the description in the message
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
      val quotes = quotesMap.keys.map(s => new Rate(s, quotesMap.get(s).get)).toList
      Success(LiveRates(timestamp, source, quotes))
    }
  }

  def historicQuoteQuery() = {
    quoteQuery() match {
      case Success(q) =>
        val date = CurrencyLayerClient.DATE_FORMATTER.parse(payload.get("date").get.asInstanceOf[String])
        Success(HistoricRates(date, q.timestamp, q.source, q.quotes))
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
        val date = CurrencyLayerClient.DATE_FORMATTER.parse(payload.get("date").get.asInstanceOf[String])
        Success(HistoricConvert(date, q.query, q.info, q.result))
      case Failure(f) => Failure(f)
    }
  }

  def ratesOverInterval() = {
    if (!isSuccess)
      Failure(parseException())
    else {
      val startDate = CurrencyLayerClient.DATE_FORMATTER.parse(payload.get("start_date").get.asInstanceOf[String])
      val endDate = CurrencyLayerClient.DATE_FORMATTER.parse(payload.get("end_date").get.asInstanceOf[String])
      val source = payload.get("source").get.asInstanceOf[String]
      val quotesMap = payload.get("quotes").get.asInstanceOf[Map[String, Any]]
      val m: Map[String, List[Rate]] = quotesMap.mapValues(blob => {
        val _m = blob.asInstanceOf[Map[String, Double]]
        _m.keys.map(k => new Rate(k, _m.get(k).get)).toList
      })
      val ratesMap = m.keys.map(k => (CurrencyLayerClient.DATE_FORMATTER.parse(k), m.get(k).get)).toMap
      Success(new RatesOverTimeQuery(source, startDate, endDate, ratesMap))
    }
  }

  def currencyChangeOverInterval() = {
    if (!isSuccess)
      Failure(parseException())
    else {
      val startDate = CurrencyLayerClient.DATE_FORMATTER.parse(payload.get("start_date").get.asInstanceOf[String])
      val endDate = CurrencyLayerClient.DATE_FORMATTER.parse(payload.get("end_date").get.asInstanceOf[String])
      val source = payload.get("source").get.asInstanceOf[String]
      val changes = payload.get("quotes").get.asInstanceOf[Map[String, Any]]
      Success(changes.keys.map(pair => {
        val values = changes.get(pair).get.asInstanceOf[Map[String, Double]]
        new CurrencyChange(
          pair,
          source,
          startDate,
          endDate,
          values.get("start_rate").get,
          values.get("end_rate").get,
          values.get("change").get,
          values.get("change_pct").get
        )
      }).toList)
    }
  }
}
