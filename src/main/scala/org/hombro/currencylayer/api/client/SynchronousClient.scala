package org.hombro.currencylayer.api.client

import java.util.Date

import org.hombro.currencylayer.CurrencyLayerClient
import org.hombro.currencylayer.api.response.json.Response

import scalaj.http.Http


/**
  * Created by nicolas on 6/11/2017.
  */
case class SynchronousClient(override val apiKey: String, val protocol: String = "http://") extends CurrencyLayerClient(apiKey) {
  // TODO code duplication
  override def supportedCurrencies() = {
    val request = Http(protocol + CurrencyLayerClient.ENDPOINT_LIST)
      .param("access_key", apiKey)
    val json = request.asString.body
    Response.parse(json).currencyList()
  }

  override def liveRate(currencies: List[String], prettyJson: Boolean) = {
    val request = Http(protocol + CurrencyLayerClient.ENDPOINT_LIVE)
      .param("access_key", apiKey)
    if (!prettyJson) {
      request.param("format", "0")
    }
    val json = request.asString.body
    Response.parse(json).quoteQuery()
  }

  override def historicalConversion(fromCurrency: String, toCurrency: String, amount: Int, date: Date, prettyJson: Boolean) = {
    val request = Http(protocol + CurrencyLayerClient.ENDPOINT_CONVERT)
      .param("access_key", apiKey)
      .param("from", fromCurrency)
      .param("to", toCurrency)
      .param("amount", amount.toString)
      .param("date", CurrencyLayerClient.DATE_FORMATTER.format(date))
    if (!prettyJson) {
      request.param("format", "0")
    }
    val json = request.asString.body
    Response.parse(json).historicConvert()
  }

  override def historicalRate(date: Date, currencies: List[String] = List(), prettyJson: Boolean) = {
    val request = Http(protocol + CurrencyLayerClient.ENDPOINT_HISTORICAL)
      .param("access_key", apiKey)
      .param("date", CurrencyLayerClient.DATE_FORMATTER.format(date))
    if (!prettyJson) {
      request.param("format", "0")
    }
    if (currencies.nonEmpty) {
      request.param("currencies", currencies.mkString(","))
    }
    val json = request.asString.body
    Response.parse(json).historicQuoteQuery()
  }

  override def liveConversion(fromCurrency: String, toCurrency: String, amount: Int, prettyJson: Boolean) = {
    val request = Http(protocol + CurrencyLayerClient.ENDPOINT_CONVERT)
      .param("access_key", apiKey)
      .param("from", fromCurrency)
      .param("to", toCurrency)
      .param("amount", amount.toString)
    if (!prettyJson) {
      request.param("format", "0")
    }
    val json = request.asString.body
    Response.parse(json).convert()
  }

  override def change(source: String, currencies: List[String], prettyJson: Boolean) = {
    val request = Http(protocol + CurrencyLayerClient.ENDPOINT_CHANGE)
      .param("access_key", apiKey)
      .param("currencies", currencies.mkString(","))
      .param("source", source)
    if (!prettyJson) {
      request.param("format", "0")
    }
    val json = request.asString.body
    Response.parse(json).currencyChangeOverInterval()
  }

  override def changeOverInterval(source: String, currencies: List[String], startDate: Date, endDate: Date, prettyJson: Boolean) = {
    val request = Http(protocol + CurrencyLayerClient.ENDPOINT_CHANGE)
      .param("access_key", apiKey)
      .param("currencies", currencies.mkString(","))
      .param("source", source)
      .param("start_date", CurrencyLayerClient.DATE_FORMATTER.format(startDate))
      .param("end_date", CurrencyLayerClient.DATE_FORMATTER.format(endDate))
    if (!prettyJson) {
      request.param("format", "0")
    }
    val json = request.asString.body
    Response.parse(json).currencyChangeOverInterval()
  }

  override def ratesOverInterval(startDate: Date, endDate: Date, source: String, currencies: List[String], prettyJson: Boolean) = {
    val request = Http(protocol + CurrencyLayerClient.ENDPOINT_TIMEFRAME)
      .param("access_key", apiKey)
      .param("start_date", CurrencyLayerClient.DATE_FORMATTER.format(startDate))
      .param("end_date", CurrencyLayerClient.DATE_FORMATTER.format(endDate))
    if (source.nonEmpty)
      request.param("source", source)
    if (currencies.nonEmpty)
      request.param("currencies", currencies.mkString(","))
    if (!prettyJson) {
      request.param("format", "0")
    }
    val json = request.asString.body
    Response.parse(json).ratesOverInterval()
  }
}
