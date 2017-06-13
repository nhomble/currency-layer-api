package org.hombro.currencylayer.api.response.json

/**
  * Created by nicolas on 6/11/2017.
  */
object Quote {
  def apply(currency: String, rate: Double) = new Quote(currency, rate)
}
class Quote(val currency: String, val rate: Double) {}
