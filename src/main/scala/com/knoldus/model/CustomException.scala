package com.knoldus.model

case class CustomException(message: String = "Directory does not exist.") extends Exception(message)
