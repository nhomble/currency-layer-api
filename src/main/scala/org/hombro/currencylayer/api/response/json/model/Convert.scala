package org.hombro.currencylayer.api.response.json.model

import java.util.Date

/**
  * Created by nicolas on 6/17/2017.
  */
sealed abstract class Convert {
  val query: ConvertQuery
  val info: ConvertInfo
  val result: Double
}

case class LiveConvert(val query: ConvertQuery, val info: ConvertInfo, val result: Double) extends Convert

case class HistoricConvert(val date: Date, val query: ConvertQuery, val info: ConvertInfo, val result: Double) extends Convert with HistoricQuery

class ConvertQuery(val from: String, val to: String, val amount: Long)

class ConvertInfo(val timestamp: Long, val quote: Double)

