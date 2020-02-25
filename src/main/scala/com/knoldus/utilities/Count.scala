package com.knoldus.utilities

import java.io.File

import com.knoldus.model.CountOfTags

import scala.io.Source

object Count {

  def count(file: File, tag: (String, String, String)): CountOfTags = {
    if (file.isFile) {
      val source = Source.fromFile(file)
      val content = source.getLines
      val occurrenceOfTagInFile = content.foldLeft((0, 0, 0))((countOfTag, lineReadFromFile) => {
        if (lineReadFromFile.contains(tag._1)) (countOfTag._1 + 1, countOfTag._2, countOfTag._3)
        else if (lineReadFromFile.contains(tag._2)) (countOfTag._1, countOfTag._2 + 1, countOfTag._3)
        else if (lineReadFromFile.contains(tag._3)) (countOfTag._1, countOfTag._2, countOfTag._3 + 1)
        else countOfTag
      })
      CountOfTags(file.getPath, occurrenceOfTagInFile._1, occurrenceOfTagInFile._2, occurrenceOfTagInFile._3)
    }
    else {
      CountOfTags("", 0, 0, 0)
    }
  }


}
