package org.hombro.currencylayer.api.response.json.model

import java.util.Date

/**
  * Created by nicolas on 6/11/2017.
  */
sealed abstract class QuoteQuery {
  val timestamp: Long
  val source: String
  val quotes: List[Quote]
}

case class LiveQuoteQuery(val timestamp: Long, val source: String, val quotes: List[Quote]) extends QuoteQuery

case class HistoricQuoteQuery(val date: Date, val timestamp: Long, val source: String, val quotes: List[Quote]) extends QuoteQuery with HistoricQuery

class Quote(val currency: String, val rate: Double)
