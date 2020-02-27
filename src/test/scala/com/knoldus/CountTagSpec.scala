package com.knoldus

import java.io.File

import com.knoldus.model.{CountOfTags, CustomException}
import org.scalatest.flatspec.AsyncFlatSpec

class CountTagSpec extends AsyncFlatSpec {

  it should "eventually return average of count of tags in non-existant directory to be 0" in {
    val obtainedResult = CountTag.countAverageTagsPerFileInADirectory("/logs", "error").recover({
      case _: CustomException => 0.toDouble
    })
    val expectedResult = 0.toDouble
    obtainedResult map { obtResult => assert(expectedResult == obtResult) }
  }
  it should "eventually return average of count of tags in valid directory" in {
    val obtainedResult = CountTag.countAverageTagsPerFileInADirectory("logs", "error").recover({
      case _: CustomException => 0.toDouble
    })
    val expectedResult = 174.0
    obtainedResult map { obtResult => assert(expectedResult == obtResult) }
  }

  it should "return count of tags in a file" in {
    val obtainedResult = CountTag.count(new File("logs"), ("error", "warn", "info"))
    val expectedResult = CountOfTags("", 0, 0, 0)
    assert(expectedResult == obtainedResult)
  }

  it should "eventually raise an exception for invalid directory" in {
    val obtainedResult = CountTag.countTotalTagsInAllFilesInADirectory("invalidDir/", "error").recover({
      case _: CustomException => 0.toDouble
    })
    val expectedResult = 0
    obtainedResult map { obtResult => assert(expectedResult == obtResult) }
  }

  it should "eventually return object of TotalTagsInDirectory with total count in a valid directory" in {
    val obtainedResult = CountTag.countTotalTagsInAllFilesInADirectory("logs", "error").recover({
      case _: CustomException => 0.toDouble
    })
    val expectedResult = CustomConfig.total
    obtainedResult map { obtResult => assert(expectedResult == obtResult) }
  }

}
