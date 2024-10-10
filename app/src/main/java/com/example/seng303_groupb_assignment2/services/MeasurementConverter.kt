package com.example.seng303_groupb_assignment2.services

import android.util.Log

class MeasurementConverter(
    private val isMetric : Boolean
) {

    // Conversion constants
    private val kgToLbConversionFactor = 2.20462f
    private val kmToMilesConversionFactor = 0.621371f

    fun convertToImperial(value: Float, type: String) : Float {
        return when (type) {
            "Weight" -> convertWeightToLb(value)
            "Distance" -> convertDistanceToMi(value)
            else -> value
        }
    }

    fun convertToMetric(value: Float, type: String) : Float {
        return when (type) {
            "Weight" -> convertWeightToKg(value)
            "Distance" -> convertDistanceToKm(value)
            else -> value
        }
    }

    private fun convertWeightToLb(valueInKg: Float): Float {
        return if (isMetric) {
            valueInKg
        } else {
            valueInKg * kgToLbConversionFactor
        }
    }

    private fun convertWeightToKg(value: Float): Float {
        return if (isMetric) {
            value
        } else {
            value / kgToLbConversionFactor
        }
    }

    private fun convertDistanceToMi(valueInKm: Float): Float {
        return if (isMetric) {
            valueInKm
        } else {
            valueInKm * kmToMilesConversionFactor
        }
    }

    private fun convertDistanceToKm(valueInMiles: Float): Float {
        return if (isMetric) {
            valueInMiles
        } else {
            valueInMiles / kmToMilesConversionFactor
        }
    }
}