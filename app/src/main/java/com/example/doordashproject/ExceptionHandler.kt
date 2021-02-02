package com.example.doordashproject

/* Rudimentary exception handling for this project */

object ExceptionHandler {
    var displayToast = { message: String -> println(message) }
    var displayDialog = { message: String -> println(message) }
    var displaySnackbar = { message: String -> println(message) }
}