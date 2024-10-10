import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.seng303_groupb_assignment2.enums.Measurement
import kotlinx.coroutines.launch
import com.example.seng303_groupb_assignment2.entities.Exercise

class ExerciseModalViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var exerciseName by mutableStateOf(savedStateHandle.get<String>("exerciseName") ?: "")
        private set

    var restTime by mutableStateOf(savedStateHandle.get<String>("restTime") ?: "")
        private set

    var measurement by mutableStateOf(savedStateHandle.get<Measurement>("measurement") ?: Measurement.REPS_WEIGHT)

    fun updateExerciseName(newName: String) {
        exerciseName = newName
        savedStateHandle["exerciseName"] = newName
    }

    fun updateRestTime(newTime: String) {
        restTime = newTime
        savedStateHandle["restTime"] = newTime
    }

    fun updateMeasurement(newMeasurement: Measurement) {
        measurement = newMeasurement
        savedStateHandle["measurement"] = newMeasurement
    }

    fun clearSavedInfo() {
        updateExerciseName("")
        updateRestTime("")
        updateMeasurement(Measurement.REPS_WEIGHT)
    }

    fun validRestTime(): Boolean {
        return restTime.isBlank() || restTime.toIntOrNull() != null
    }

    fun validExerciseName(): Boolean {
        return exerciseName.isNotBlank()
    }

//    fun setupFromExercise(exercise: Exercise) {
//        updateExerciseName(exercise.name)
//        updateSets(exercise.sets.toString())
//        updateMeasurementType1(exercise.measurement1.type)
//        updateMeasurementValues1(exercise.measurement1.values.map { it.toString() })
//        updateMeasurementType2(exercise.measurement2.type)
//        updateMeasurementValues2(exercise.measurement2.values.map { it.toString() })
//
//        if (exercise.restTime == null) {
//            updateRestTime("")
//        } else {
//            updateRestTime(exercise.restTime.toString())
//        }
//    }
//
//    fun clear() {
//        updateExerciseName("")
//        updateSets("")
//        updateMeasurementType1("")
//        updateMeasurementValues1(listOf())
//        updateMeasurementType2("")
//        updateMeasurementValues2(listOf())
//        updateRestTime("")
//    }
}