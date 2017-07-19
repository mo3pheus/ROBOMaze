/**
  * Created by sanketkorgaonkar on 7/18/17.
  */


package com.egen.training

import scala.util.{Failure, Success, Try}

object ScalaTraining extends App {
  println("Hello World")

  val coordinate = (42.34, 34.45)

  val temp1 = 4

  def Coordinate(latitude: (Double, Double)) = {}

  val anotherCoordinate = Coordinate(34.45, 23.23)

  val name = "Sanket"
  val height = 23.34
  println(s"I am a wonderful person - my name is $name my height is ($height)")
  buggyFunc()

  def returnInt(): Int = {
    val temp = 4
    if (temp == 4) {
      throw new RuntimeException("Issues")
    } else {
      throw new Exception("Issues here");
    }
  }

  def buggyFunc(): Try[Int] = {
    try {
      println("hello")
      print()
      Success(returnInt())
    } catch {
      case e: Exception => Failure(e)
      case r: RuntimeException => Failure(r)
    }
  }

}
