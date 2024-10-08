package com.example.seng303_groupb_assignment2.services

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FileExportService(private val context: Context) {

    /**
     * Exports the provided data in CSV format to the shared Documents folder.
     *
     * @param fileName Name of the file to be exported
     * @param csvData Data to be written into the CSV file
     * @return The absolute path of the exported file
     */
    fun exportToCsv(fileName: String, csvData: String): String? {
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

        if (!documentsDir.exists()) {
            documentsDir.mkdirs()
        }

        val file = File(documentsDir, fileName)

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
     * Helper function to prepare CSV data
     *
     * @param headers A list of column headers for the workout
     * @param rows A list of rows where each row is a list of column values
     * @return A string in CSV format
     */
    fun prepareCsvData(headers: List<String>, rows: List<List<Any>>): String {
        val header = headers.joinToString(",") + "\n"
        val data = rows.joinToString("\n") { it.joinToString(",") }
        return header + data
    }


}
