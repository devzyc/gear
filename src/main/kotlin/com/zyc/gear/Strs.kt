package com.zyc.gear

fun String.occurTimesOf(subStr: String) =
    windowed(subStr.length) {
        if (it == subStr) 1 else 0
    }.sum()

fun String.isNumeric(): Boolean {
    return try {
        toInt()
        true
    } catch (e: NumberFormatException) {
        false
    }
}