package org.hombro.currencylayer

import java.text.SimpleDateFormat
import java.util.Date

import org.hombro.currencylayer.api.response.error.CurrencyLayerError.CurrencyLayerError
import org.hombro.currencylayer.api.response.json.{HistoricQuoteQuery, InstantQuoteQuery, CurrencyList}

object CurrencyLayerClient {
  private val BASE_URL = "apilayer.net/api"

  val ENDPOINT_LIVE = BASE_URL + "/live"
  val ENDPOINT_HISTORICAL = BASE_URL + "/historical"
  val ENDPOINT_CONVERT = BASE_URL + "/convert"
  val ENDPOINT_TIMEFRAME = BASE_URL + "/timeframe"
  val ENDPOINT_CHANGE = BASE_URL + "/change"
  val ENDPOINT_LIST = BASE_URL + "/list"

  val DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd")
}

/**
  * Created by nicolas on 6/11/2017.
  * https://currencylayer.com/
  */
abstract class CurrencyLayerClient(val apiKey: String) {
  /**
    * "live" endpoint - request the most recent exchange rate data
    *
    * @param currencies
    * @param prettyJson
    * @return
    */
  def liveRate(currencies: List[String] = List(), prettyJson: Boolean = true): Either[CurrencyLayerError, InstantQuoteQuery]

  /**
    * "historical" endpoint - request historical rates for a specific day
    *
    * @param date
    * @param currencies
    * @param prettyJson
    * @return
    */
  def historicalRate(date: Date, currencies: List[String] = List(), prettyJson: Boolean = true): Either[CurrencyLayerError, HistoricQuoteQuery]

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
  def liveConversion(fromCurrency: String, toCurrency: String, amount: Int, prettyJson: Boolean = true)

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
  def historialConversion(fromCurrency: String, toCurrency: String, amount: Int, date: Date, prettyJson: Boolean = true)

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
  def ratesOverInterval(startDate: Date, endDate: Date, source: String, currencies: List[String] = List(), prettyJson: Boolean = true)

  /**
    * "change" endpoint - request any currency's change parameters (margin
    * and percentage), optionally between two specified dates
    *
    * @param source
    * @param currencies
    * @param prettyJson
    * @return
    */
  def change(source: String, currencies: List[String] = List(), prettyJson: Boolean = true)

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
  def changeOverInterval(source: String, currencies: List[String] = List(), startDate: Date, endDate: Date, prettyJson: Boolean = true)

  /**
    * Return all supported currencies
    *
    * @return
    */
  def supportedCurrencies(): Either[CurrencyLayerError, CurrencyList]
}
