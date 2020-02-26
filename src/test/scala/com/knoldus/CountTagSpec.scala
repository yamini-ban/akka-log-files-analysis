//package com.knoldus
//
//import com.knoldus.model.CustomException
//import org.scalatest.flatspec.AsyncFlatSpec
//
//class CountTagSpec extends AsyncFlatSpec {
//
//  it should "eventually return average of count of tags in non-existant directory to be 0" in {
//    val obtainedResult = CountTag.countAverageTagsPerFileInADirectory("/logs", "error").recover({
//      case _: CustomException => 0.toDouble
//    })
//    val expectedResult = 0.toDouble
//    obtainedResult map { obtResult => assert(expectedResult == obtResult) }
//  }
//
//}
