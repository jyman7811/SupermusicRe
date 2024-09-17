package org.example.exception

sealed class CommandException(val msg: String) : Exception()