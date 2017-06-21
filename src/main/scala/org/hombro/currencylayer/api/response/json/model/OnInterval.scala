package org.hombro.currencylayer.api.response.json.model

import java.util.Date

/**
  * Created by nicolas on 6/21/2017.
  */
trait OnInterval {
  val startDate: Date
  val endDate: Date
}
