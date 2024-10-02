package com.example.seng303_groupb_assignment2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.seng303_groupb_assignment2.daos.ExerciseDao
import com.example.seng303_groupb_assignment2.daos.ExerciseLogDao
import com.example.seng303_groupb_assignment2.daos.WorkoutDao
import com.example.seng303_groupb_assignment2.entities.Exercise
import com.example.seng303_groupb_assignment2.entities.ExerciseLog
import com.example.seng303_groupb_assignment2.entities.Workout
import com.example.seng303_groupb_assignment2.entities.WorkoutExerciseCrossRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Workout::class, Exercise::class, ExerciseLog::class, WorkoutExerciseCrossRef::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseLogDao(): ExerciseLogDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gym_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    // Run your pre-population in IO thread
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.exerciseDao(), database.workoutDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(exerciseDao: ExerciseDao, workoutDao: WorkoutDao) {
            // Create dummy exercises
            val exercise1 = Exercise(name = "Squat", sets = 3, reps = listOf(10, 12, 15), weight = listOf(60f, 65f, 70f), restTime = 90)
            val exercise2 = Exercise(name = "Leg Press", sets = 4, reps = listOf(10, 12, 15, 15), weight = listOf(100f, 110f, 120f, 130f), restTime = 90)

            // Insert exercises into the database
            exerciseDao.upsertExercise(exercise1)
            exerciseDao.upsertExercise(exercise2)

            // Create a workout and cross-reference the exercises
            val workout = Workout(name = "Lower Body", description = "lower body exercise", schedule = null)
            workoutDao.upsertWorkout(workout)
            workoutDao.upsertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workout.id, exercise1.id))
            workoutDao.upsertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workout.id, exercise2.id))
        }
    }
}