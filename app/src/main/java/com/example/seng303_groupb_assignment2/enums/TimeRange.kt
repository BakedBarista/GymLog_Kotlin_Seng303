package com.example.seng303_groupb_assignment2.enums

enum class TimeRange(val label: String, val days: Long?) {
    LAST_MONTH("1m", 30),
    LAST_3_MONTHS("3m", 90),
    LAST_6_MONTHS("6m", 180),
    LAST_YEAR("1y", 365),
    ALL("All", null)
}