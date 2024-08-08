package com.achmadss.prodiainterview.ui.common

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.formatDateTime(): String {
    val inputFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)

    val dateTime = ZonedDateTime.parse(this, inputFormatter)
    return dateTime.format(outputFormatter)
}
