package org.hombro.currencylayer.api.response.error

/**
  * Created by nicolas on 6/11/2017.
  */
object CurrencyLayerError {

  class CurrencyLayerError(val code: Int, val message: String) extends Exception(message) {
    override def toString = "%d: %s".format(code, message)

  }

  val ResourceDoesNotExist = new CurrencyLayerError(404, "User requested a resource which does not exist.")
  val InvalidAPIKey = new CurrencyLayerError(101, "Invalid API key supplied.")
  val APIFunctionDoesNotExist = new CurrencyLayerError(103, "User requested a non-existent API function.")
  val MonthlyAllowanceReached = new CurrencyLayerError(104, "User has reached or exceeded his subscription plan's monthly API request allowance.")
  val UserNotEntitled = new CurrencyLayerError(105, "The user's current subscription plan does not support the requested API Function.")
  val EmpyResults = new CurrencyLayerError(106, "The user's query did not return any results.")
  val InvalidDateFormat = new CurrencyLayerError(302, "You have entered an invalid date")
  private val allExceptions = List(ResourceDoesNotExist, InvalidAPIKey, APIFunctionDoesNotExist, MonthlyAllowanceReached, UserNotEntitled, EmpyResults)

  def getException(code: Int) = allExceptions.find(e => e.code == code) match {
    case Some(e) => e
    case None => new CurrencyLayerError(code, "Unknown error code thrown")
  }
}