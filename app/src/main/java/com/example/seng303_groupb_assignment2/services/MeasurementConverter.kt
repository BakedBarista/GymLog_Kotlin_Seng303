package com.example.seng303_groupb_assignment2.services

import com.example.seng303_groupb_assignment2.entities.ExerciseLog

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

    fun convertSetToImperial(value: List<Pair<Float, Float>>, type: String): List<Pair<Float, Float>> {
        return when (type) {
            "Weight" -> convertSetWeightsToLbs(value)
            "Distance" -> convertSetDistancesToMi(value)
            else -> value
        }
    }

    fun convertSetToMetric(value: List<Pair<Float, Float>>, type: String): List<Pair<Float, Float>> {
        return when (type) {
            "Weight" -> convertSetWeightsToKg(value)
            "Distance" -> convertSetDistancesToKm(value)
            else -> value
        }
    }

    private fun convertSetWeightsToKg(value: List<Pair<Float, Float>>): List<Pair<Float, Float>> {
        return value.map { pair ->
            Pair( pair.first, convertWeightToKg(pair.second)) // Convert only the weight (first)
        }
    }

    private fun convertSetDistancesToKm(value: List<Pair<Float, Float>>): List<Pair<Float, Float>> {
        return value.map { pair ->
            Pair(convertDistanceToKm(pair.first), pair.second)
        }
    }

    // Convert a list of weight pairs to pounds
    private fun convertSetWeightsToLbs(value: List<Pair<Float, Float>>): List<Pair<Float, Float>> {
        return value.map { pair ->
            Pair(convertWeightToLb(pair.first), pair.second)
        }
    }

    // Convert a list of distance pairs to miles
    private fun convertSetDistancesToMi(value: List<Pair<Float, Float>>): List<Pair<Float, Float>> {
        return value.map { pair ->
            Pair(pair.first, convertDistanceToMi(pair.second))
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