package org.hombro.currencylayer.api.response.json

object Currency {
  def apply(enumeration: String, description: String) = new Currency(enumeration, description)
}

/**
  * Created by nicolas on 6/11/2017.
  */
class Currency(val enumeration: String, val description: String) {

}
