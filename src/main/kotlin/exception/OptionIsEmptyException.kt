package org.example.exception


/**
 * Thrown when the required options are empty.
 */
class OptionIsEmptyException(val reply: String) : CommandException("Provided option is empty.")