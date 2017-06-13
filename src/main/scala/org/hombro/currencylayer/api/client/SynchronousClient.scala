package org.hombro.currencylayer.api.client

import java.util.Date

import org.hombro.currencylayer.CurrencyLayerClient
import org.hombro.currencylayer.api.response.json.Response

import scalaj.http.Http


/**
  * Created by nicolas on 6/11/2017.
  */
case class SynchronousClient(override val apiKey: String, val protocol: String = "http://") extends CurrencyLayerClient(apiKey) {
  /**
    * Return all supported currencies
    *
    * @return
    */
  override def supportedCurrencies() = {
    val request = Http(protocol + CurrencyLayerClient.ENDPOINT_LIST)
      .param("access_key", apiKey)
    val json = request.asString.body
    Response.parse(json).currencyList()
  }

  /**
    * "live" endpoint - request the most recent exchange rate data
    *
    * @param currencies
    * @param prettyJson
    * @return
    */
  override def liveRate(currencies: List[String], prettyJson: Boolean) = {
    val request = Http(protocol + CurrencyLayerClient.ENDPOINT_LIVE)
      .param("access_key", apiKey)
    if (!prettyJson) {
      request.param("format", "0")
    }
    val json = request.asString.body
    Response.parse(json).quoteQuery()
  }

  /**
    * identical to liveConversion(), the optional historical date parameter was pulled out and given a separate method
    * for the sake of clarity
    *
    * @param fromCurrency
    * @param toCurrency
    * @param amount
    * @param date
    * @param prettyJson
    * @return
    */
  override def historialConversion(fromCurrency: String, toCurrency: String, amount: Int, date: Date, prettyJson: Boolean) = ???

  /**
    * "historical" endpoint - request historical rates for a specific day
    *
    * @param date
    * @param currencies
    * @param prettyJson
    * @return
    */
  override def historicalRate(date: Date, currencies: List[String] = List(), prettyJson: Boolean) = {
    val request = Http(protocol + CurrencyLayerClient.ENDPOINT_HISTORICAL)
      .param("access_key", apiKey)
      .param("date", CurrencyLayerClient.DATE_FORMATTER.format(date))
    if (!prettyJson) {
      request.param("format", "0")
    }
    if(currencies.nonEmpty){
      request.param("currencies", currencies.mkString(","))
    }
    val json = request.asString.body
    Response.parse(json).historicQuoteQuery()
  }

  /**
    * "convert" endpoint - convert any amount from one currency to another
    * using real-time exchange rates
    *
    * @param fromCurrency
    * @param toCurrency
    * @param amount
    * @param prettyJson
    * @return
    */
  override def liveConversion(fromCurrency: String, toCurrency: String, amount: Int, prettyJson: Boolean) = ???

  /**
    * "change" endpoint - request any currency's change parameters (margin
    * and percentage), optionally between two specified dates
    *
    * @param source
    * @param currencies
    * @param prettyJson
    * @return
    */
  override def change(source: String, currencies: List[String], prettyJson: Boolean) = ???

  /**
    * "change" endpoint - request any currency's change parameters (margin
    * and percentage), optionally between two specified dates
    * The optional start/end dates were pulled out and given a separate method for the sake of clarity
    *
    * @param source
    * @param currencies
    * @param startDate
    * @param endDate
    * @param prettyJson
    * @return
    */
  override def changeOverInterval(source: String, currencies: List[String], startDate: Date, endDate: Date, prettyJson: Boolean) = ???

  /**
    * "timeframe" endpoint - request exchange rates for a specific period of time
    *
    * @param startDate
    * @param endDate
    * @param source
    * @param currencies
    * @param prettyJson
    * @return
    */
  override def ratesOverInterval(startDate: Date, endDate: Date, source: String, currencies: List[String], prettyJson: Boolean) = ???
}
