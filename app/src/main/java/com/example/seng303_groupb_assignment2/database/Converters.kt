package com.example.seng303_groupb_assignment2.database

import androidx.room.TypeConverter
import com.example.seng303_groupb_assignment2.enums.Days

/**
 * This is used to convert to and from lists - looks like Rooms does not support list storage
 * so this is how we take a list and store it in the DB as a string, and then take the stored string
 * and convert it back to a list. Currently there are converters for Int and Float lists, but we can
 * add converters as needed
 */
class Converters {
    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.joinToString(separator = ",")
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.split(",")?.map { it.toInt() }
    }

    @TypeConverter
    fun fromFloatList(value: List<Float>?): String? {
        return value?.joinToString(separator = ",")
    }

    @TypeConverter
    fun toFloatList(value: String?): List<Float>? {
        return value?.split(",")?.map { it.toFloat() }
    }

    @TypeConverter
    fun fromDayList(value: List<Days>?): String? {
        return value?.joinToString(separator = ",") { it.name }
    }

    @TypeConverter
    fun toDayList(value: String?): List<Days>? {
        return value?.split(",")?.map { Days.valueOf(it) }
    }
}