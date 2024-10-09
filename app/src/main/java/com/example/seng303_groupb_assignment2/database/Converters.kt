package com.example.seng303_groupb_assignment2.database

import androidx.room.TypeConverter
import com.example.seng303_groupb_assignment2.enums.Days
import com.example.seng303_groupb_assignment2.enums.Measurement

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
    fun toDayList(value: String?): List<Days> {
        if (value.isNullOrEmpty()) {
            return listOf()
        }

        return value.split(",").map { Days.valueOf(it) }
    }

//    @TypeConverter
//    fun fromMeasurement(value: Measurement?): String? {
//        return value?.let { it.type + ";" + it.values.joinToString(separator = ",") }
//    }
//
//    // todo fix this
//    @TypeConverter
//    fun toMeasurement(value: String?): Measurement? {
//        if (value.isNullOrEmpty()) return null
//        val typeAndValues = value.split(";")
//
//        if (typeAndValues.size < 2) return null
//
//        val type = typeAndValues[0]
//        val values = typeAndValues[1].split(",").mapNotNull { it.toFloatOrNull() }
//        return Measurement(type, values)
//    }

    @TypeConverter
    fun fromMeasurement(measurement: Measurement): String {
        return measurement.label
    }

    @TypeConverter
    fun toMeasurement(value: String): Measurement {
        return Measurement.entries.first { it.label == value }
    }

    @TypeConverter
    fun fromRecord(record: List<Pair<Float, Float>>): String {
        return record.joinToString(separator = ";") { "${it.first},${it.second}" }
    }

    @TypeConverter
    fun toRecord(value: String): List<Pair<Float, Float>> {
        return value.split(";").map {
            val (first, second) = it.split(",")
            Pair(first.toFloat(), second.toFloat())
        }
    }
}