package org.hombro.currencylayer.api.response.json

object CurrencyList {
  def apply(currencies: List[Currency]) = new CurrencyList(currencies)
}

/**
  * Created by nicolas on 6/11/2017.
  */
class CurrencyList(val currencies: List[Currency]) {

}
