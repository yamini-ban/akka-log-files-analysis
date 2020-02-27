# Assignment akka-log-files-analysis

Scala code for log analysis is in branch "feature/scala-implementation"

Akka code for log analysis is in branch "feature/akka-implementation"

# Basic flow of primary actor (name = Receiver) of the akka code

    1. A message of type case class AverageCount => would create a router actor of 
        Receiver with given number of child actors which would in turn work on every 
        file in a directory. And it return average count of given tag per file in a 
        directory 
     
    2. A message of type case class TagsCountMessage => would return the count of given
        tags in an object of case class CountOfTags from the given file.
    
    3. A message of type case class DirNameMessage => would return the list of existing
        file names in the given directory path else would return an empty list of String
        
    4. A message of type case class TotalTagCountMessage => returns the total count of
        given tags from all the files in the directory by creating a router that would 
        give the count of tags for each file.
        
    5. A message of type case class SchedulerMessage => would do what a scheduler wants.
    
    6. Anything other than above would get into default case.
