package com.example.seng303_groupb_assignment2.enums

import java.time.DayOfWeek
import java.time.LocalDateTime

enum class Days {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;

    fun toDayOfWeek(): DayOfWeek {
        return when (this) {
            SUNDAY -> DayOfWeek.SUNDAY
            MONDAY -> DayOfWeek.MONDAY
            TUESDAY -> DayOfWeek.TUESDAY
            WEDNESDAY -> DayOfWeek.WEDNESDAY
            THURSDAY -> DayOfWeek.THURSDAY
            FRIDAY -> DayOfWeek.FRIDAY
            SATURDAY -> DayOfWeek.SATURDAY
        }
    }

    companion object {
        fun getCurrentDay(): Days {
            val currentDay = LocalDateTime.now().dayOfWeek
            return entries.first { it.toDayOfWeek() == currentDay }
        }
    }
}