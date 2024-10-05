package com.example.seng303_groupb_assignment2.services

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FileExportService(private val context: Context) {

    /**
     * Exports the provided data in CSV format to the app's specific storage directory.
     *
     * @param fileName Name of the file to be exported (e.g., "exported_data.csv").
     * @param csvData Data to be written into the CSV file.
     * @return The absolute path of the exported file.
     */
    fun exportToCsv(fileName: String, csvData: String): String? {
        val file: File = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // Scoped Storage (App-specific storage)
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        } else {
            // Before Android 10 (Legacy storage)
            File(Environment.getExternalStorageDirectory(), fileName)
        }

        return try {
            FileOutputStream(file).use { outputStream ->
                outputStream.write(csvData.toByteArray())
                outputStream.flush()
                Log.d("FileExportService", "CSV saved to ${file.absolutePath}")
                file.absolutePath
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("FileExportService", "Error saving CSV", e)
            null
        }
    }

    /**
     * Helper function to prepare CSV data from a list of data entries.
     * Modify this function based on the structure of your data.
     *
     * @param headers A list of column headers for the CSV.
     * @param rows A list of rows where each row is a list of column values.
     * @return A string in CSV format.
     */
    fun prepareCsvData(headers: List<String>, rows: List<List<String>>): String {
        val header = headers.joinToString(",") + "\n"
        val data = rows.joinToString("\n") { it.joinToString(",") }
        return header + data
    }
}
