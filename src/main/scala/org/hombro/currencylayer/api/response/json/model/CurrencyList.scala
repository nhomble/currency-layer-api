package org.hombro.currencylayer.api.response.json.model

import java.util.Date

object CurrencyList {
  def apply(currencies: List[Currency]) = new CurrencyList(currencies)
}

/**
  * Created by nicolas on 6/11/2017.
  */
class CurrencyList(val currencies: List[Currency])

class Currency(val enumeration: String, val description: String)

class CurrencyChange(val pair: String,
                     val source: String,
                     val startDate: Date,
                     val endDate: Date,
                     val startRate: Double,
                     val endRate: Double,
                     val change: Double,
                     val changePercentage: Double) extends OnInterval