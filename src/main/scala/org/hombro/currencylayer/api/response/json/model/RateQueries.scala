package org.hombro.currencylayer.api.response.json.model

import java.util.Date

/**
  * Created by nicolas on 6/11/2017.
  */
case class LiveRates(val timestamp: Long, val source: String, val quotes: List[Rate]) extends Query

case class HistoricRates(val date: Date, val timestamp: Long, val source: String, val quotes: List[Rate]) extends Query with HistoricQuery

class RatesOverTimeQuery(val source: String, val startDate: Date, val endDate: Date, val rates: Map[Date, List[Rate]]) extends OnInterval

class Rate(val currency: String, val rate: Double)
