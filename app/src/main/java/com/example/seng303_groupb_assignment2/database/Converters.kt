package com.example.seng303_groupb_assignment2.database

import androidx.room.TypeConverter
import com.example.seng303_groupb_assignment2.entities.Measurement

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
    fun fromMeasurement(value: Measurement?): String? {
        return value?.let { it.type + ";" + it.values.joinToString(separator = ",") }
    }

    @TypeConverter
    fun toMeasurement(value: String?): Measurement? {
        if (value.isNullOrEmpty()) return null
        val typeAndValues = value.split(";")

        if (typeAndValues.size < 2) return null

        val type = typeAndValues[0]
        val values = typeAndValues[1].split(",").mapNotNull { it.toFloatOrNull() }
        return Measurement(type, values)
    }
}