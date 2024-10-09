import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.Measurement
import kotlinx.coroutines.launch

class ExerciseModalViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var exerciseName by mutableStateOf(savedStateHandle.get<String>("exerciseName") ?: "")
        private set

    var sets by mutableStateOf(savedStateHandle.get<String>("sets") ?: "")
        private set

    var measurementType1 by mutableStateOf(savedStateHandle.get<String>("measurementType1") ?: "")
        private set

    var measurementValues1 by mutableStateOf(savedStateHandle.get<List<String>>("measurementValues1") ?: listOf())
        private set

    var measurementType2 by mutableStateOf(savedStateHandle.get<String>("measurementType2") ?: "")
        private set

    var measurementValues2 by mutableStateOf(savedStateHandle.get<List<String>>("measurementValues2") ?: listOf())
        private set

    var restTime by mutableStateOf(savedStateHandle.get<String>("restTime") ?: "")
        private set

    fun updateExerciseName(newName: String) {
        exerciseName = newName
        savedStateHandle["exerciseName"] = newName
    }

    fun updateSets(newSets: String) {
        sets = newSets
        savedStateHandle["sets"] = newSets

        if (sets.isNotBlank()) {
            val setsSize = sets.toInt()
            val measurementValues1 = measurementValues1.toMutableList().apply {
                while (size != setsSize) {
                    if (size < setsSize) add("")
                    else removeLast()
                }
            }
            updateMeasurementValues1(measurementValues1)

            val measurementValues2 = measurementValues2.toMutableList().apply {
                while (size != setsSize) {
                    if (size < setsSize) add("")
                    else removeLast()
                }
            }
            updateMeasurementValues2(measurementValues2)
        }
    }

    fun updateMeasurementType1(newType: String) {
        measurementType1 = newType
        savedStateHandle["measurementType1"] = newType
    }

    fun updateMeasurementValues1(newValues: List<String>) {
        measurementValues1 = newValues
        savedStateHandle["measurementValues1"] = newValues
    }

    fun updateMeasurementType2(newType: String) {
        measurementType2 = newType
        savedStateHandle["measurementType2"] = newType
    }

    fun updateMeasurementValues2(newValues: List<String>) {
        measurementValues2 = newValues
        savedStateHandle["measurementValues2"] = newValues
    }

    fun updateRestTime(newTime: String) {
        restTime = newTime
        savedStateHandle["restTime"] = newTime
    }

    fun clearSavedInfo() {
        updateExerciseName("")
        updateSets("")
        updateMeasurementType1("")
        updateMeasurementType2("")
        updateMeasurementValues1(mutableListOf())
        updateMeasurementValues2(mutableListOf())
        updateRestTime("")
    }

    fun validMeasurementValues(): Boolean {
        return measurementValues1.all { it.toFloatOrNull() != null } && measurementValues2.all { it.toFloatOrNull() != null }
    }

    fun validSetValue(): Boolean {
        return sets.isNotBlank() && sets.toFloatOrNull() != null
    }

    fun validRestTime(): Boolean {
        return restTime.isBlank() || restTime.toIntOrNull() != null
    }

    fun validExerciseName(): Boolean {
        return exerciseName.isNotBlank()
    }

    fun setupFromExercise(exercise: Exercise) {
        updateExerciseName(exercise.name)
        updateSets(exercise.sets.toString())
        updateMeasurementType1(exercise.measurement1.type)
        updateMeasurementValues1(exercise.measurement1.values.map { it.toString() })
        updateMeasurementType2(exercise.measurement2.type)
        updateMeasurementValues2(exercise.measurement2.values.map { it.toString() })

        if (exercise.restTime == null) {
            updateRestTime("")
        } else {
            updateRestTime(exercise.restTime.toString())
        }
    }

    fun clear() {
        updateExerciseName("")
        updateSets("")
        updateMeasurementType1("")
        updateMeasurementValues1(listOf())
        updateMeasurementType2("")
        updateMeasurementValues2(listOf())
        updateRestTime("")
    }
}