import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import com.example.seng303_groupb_assignment2.enums.Measurement

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

}