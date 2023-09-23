package de.xorg.gsapp.ui.tools

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

private val curYear = Clock.System.todayIn(TimeZone.currentSystemDefault()).year % 100

val classList: List<String> = listOf(
    "5.1", "5.2", "5.3", "5.4", "5.5",
    "6.1", "6.2", "6.3", "6.4", "6.5",
    "7.1", "7.2", "7.3", "7.4", "7.5",
    "8.1", "8.2", "8.3", "8.4", "8.5",
    "9.1", "9.2", "9.3", "9.4", "9.5",
    "10.1", "10.2", "10.3", "10.4", "10.5",
    "A${curYear}", "A${curYear+1}", "A${curYear+2}")