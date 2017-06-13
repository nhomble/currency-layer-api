package org.hombro.currencylayer.api.response.json

import java.util.Date

/**
  * Created by nicolas on 6/11/2017.
  */
sealed abstract class QuoteQuery(val timestamp: Double, val source: String, val quotes: List[Quote]) {}

case class InstantQuoteQuery(override val timestamp: Double, override val source: String, override val quotes: List[Quote]) extends QuoteQuery(timestamp, source, quotes) {}

case class HistoricQuoteQuery(val date: Date, override val timestamp: Double, override val source: String, override val quotes: List[Quote]) extends QuoteQuery(timestamp, source, quotes) {}
