package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.CycleEntity
import com.example.data.model.LogEntity
import com.example.data.model.UserEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MedicalReportExporter {

    fun generatePdfFile(
        context: Context,
        user: UserEntity,
        cycles: List<CycleEntity>,
        logs: List<LogEntity>
    ): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 standard dimensions
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        var y = 45f

        // Document Header
        paint.color = Color.rgb(30, 27, 41)
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("LunaCycle Medical Health Report", 40f, y, paint)
        y += 20f

        paint.color = Color.rgb(108, 92, 231)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val dateStr = SimpleDateFormat("MMMM dd, yyyy - HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("CONFIDENTIAL MEDICAL SUMMARY • GENERATED ON $dateStr", 40f, y, paint)
        y += 18f

        // Divider
        paint.color = Color.rgb(220, 220, 230)
        paint.strokeWidth = 1.5f
        canvas.drawLine(40f, y, 555f, y, paint)
        y += 24f

        // Patient Overview Box
        paint.color = Color.rgb(248, 247, 252)
        canvas.drawRoundRect(40f, y, 555f, y + 60f, 10f, 10f, paint)

        paint.color = Color.rgb(50, 45, 65)
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("PATIENT PROFILE:", 54f, y + 24f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val irregularityText = if (user.isIrregular) "Irregular" else "Regular"
        canvas.drawText("Name: ${user.name.ifBlank { "User" }}   |   Age: ${user.age}   |   Avg Cycle: ${user.cycleLength} days   |   Avg Period: ${user.periodLength} days ($irregularityText)", 54f, y + 44f, paint)
        y += 80f

        // Cycle Summary Statistics
        paint.color = Color.rgb(30, 27, 41)
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("1. Cycle History & Regularity (Past Cycles: ${cycles.size})", 40f, y, paint)
        y += 16f

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = Color.rgb(80, 75, 95)
        if (cycles.isEmpty()) {
            canvas.drawText("No completed cycles recorded yet. Estimated cycle: ${user.cycleLength} days.", 40f, y, paint)
            y += 18f
        } else {
            cycles.takeLast(4).reversed().forEach { cycle ->
                val end = cycle.endDate ?: "In Progress"
                canvas.drawText("• Start: ${cycle.startDate}  →  End: $end  |  Duration: ${cycle.length} days", 48f, y, paint)
                y += 16f
            }
        }
        y += 12f

        // Symptoms Frequency Analysis
        paint.color = Color.rgb(30, 27, 41)
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("2. Reported Symptoms & Severity Breakdown", 40f, y, paint)
        y += 16f

        val allSymptoms = logs.flatMap { it.symptoms }
        val symptomCounts = allSymptoms.groupingBy { it }.eachCount().toList().sortedByDescending { it.second }

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = Color.rgb(80, 75, 95)

        if (symptomCounts.isEmpty()) {
            canvas.drawText("No specific symptoms reported in recent logs.", 40f, y, paint)
            y += 18f
        } else {
            val symptomSummary = symptomCounts.take(6).joinToString("   |   ") { "${it.first}: ${it.second} times" }
            canvas.drawText(symptomSummary, 48f, y, paint)
            y += 20f
        }
        y += 10f

        // Menstrual Flow and Daily Vitality
        paint.color = Color.rgb(30, 27, 41)
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("3. Flow Intensity & Mood Patterns", 40f, y, paint)
        y += 16f

        val periodDays = logs.count { it.isPeriod }
        val heavyDays = logs.count { it.intensity.equals("Heavy", ignoreCase = true) }
        val mediumDays = logs.count { it.intensity.equals("Medium", ignoreCase = true) }
        val lightDays = logs.count { it.intensity.equals("Light", ignoreCase = true) }

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = Color.rgb(80, 75, 95)
        canvas.drawText("Total Period Days Logged: $periodDays   (Heavy: $heavyDays, Medium: $mediumDays, Light: $lightDays)", 48f, y, paint)
        y += 26f

        // Recent Logs Table (Latest 6 logs)
        paint.color = Color.rgb(30, 27, 41)
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("4. Recent Daily Entries (Latest Records)", 40f, y, paint)
        y += 16f

        // Table Header
        paint.color = Color.rgb(235, 232, 245)
        canvas.drawRoundRect(40f, y, 555f, y + 20f, 4f, 4f, paint)

        paint.color = Color.rgb(40, 35, 55)
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("DATE", 46f, y + 14f, paint)
        canvas.drawText("FLOW", 115f, y + 14f, paint)
        canvas.drawText("MOOD", 180f, y + 14f, paint)
        canvas.drawText("SYMPTOMS", 255f, y + 14f, paint)
        canvas.drawText("BBT / FLUID", 430f, y + 14f, paint)
        y += 24f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 8.5f
        paint.color = Color.rgb(60, 55, 75)

        val latestLogs = logs.takeLast(7).reversed()
        if (latestLogs.isEmpty()) {
            canvas.drawText("No daily log records entered yet.", 46f, y + 14f, paint)
            y += 24f
        } else {
            latestLogs.forEach { log ->
                val flowStr = if (log.isPeriod) "Period (${log.intensity})" else "None"
                val symptomsStr = if (log.symptoms.isEmpty()) "None" else log.symptoms.take(3).joinToString(", ")
                val bbtStr = "${log.bbt?.let { "${it}°C" } ?: "--"} / ${log.cervicalMucus ?: "--"}"

                canvas.drawText(log.date, 46f, y + 12f, paint)
                canvas.drawText(flowStr, 115f, y + 12f, paint)
                canvas.drawText(log.mood, 180f, y + 12f, paint)
                canvas.drawText(symptomsStr, 255f, y + 12f, paint)
                canvas.drawText(bbtStr, 430f, y + 12f, paint)

                paint.color = Color.rgb(240, 240, 245)
                canvas.drawLine(40f, y + 18f, 555f, y + 18f, paint)
                paint.color = Color.rgb(60, 55, 75)
                y += 20f
            }
        }
        y += 20f

        // Doctor Notes Section
        paint.color = Color.rgb(248, 248, 252)
        canvas.drawRoundRect(40f, y, 555f, y + 70f, 8f, 8f, paint)
        paint.color = Color.rgb(180, 180, 200)
        paint.style = Paint.Style.STROKE
        canvas.drawRoundRect(40f, y, 555f, y + 70f, 8f, 8f, paint)
        paint.style = Paint.Style.FILL

        paint.color = Color.rgb(120, 115, 135)
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        canvas.drawText("Physician / Healthcare Provider Notes:", 52f, y + 20f, paint)

        // Footer
        paint.color = Color.rgb(150, 145, 165)
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Generated by LunaCycle • This document is for clinical reference and does not substitute professional medical diagnosis.", 40f, 815f, paint)

        pdfDocument.finishPage(page)

        // Save PDF to cache
        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()
        val pdfFile = File(reportsDir, "LunaCycle_Medical_Report.pdf")
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
        return pdfFile
    }

    fun generateCsvFile(
        context: Context,
        user: UserEntity,
        cycles: List<CycleEntity>,
        logs: List<LogEntity>
    ): File {
        val sb = StringBuilder()
        sb.append("Date,Period,Intensity,Symptoms,Mood,Water_ml,BBT_Celsius,Cervical_Mucus,Notes\n")
        logs.sortedBy { it.date }.forEach { log ->
            val symptomsEscaped = "\"${log.symptoms.joinToString("; ")}\""
            val notesEscaped = "\"${log.notes.replace("\"", "\"\"")}\""
            sb.append("${log.date},${log.isPeriod},${log.intensity},$symptomsEscaped,${log.mood},${log.waterMl},${log.bbt ?: ""},${log.cervicalMucus ?: ""},$notesEscaped\n")
        }

        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()
        val csvFile = File(reportsDir, "LunaCycle_Health_Data.csv")
        csvFile.writeText(sb.toString())
        return csvFile
    }

    fun generateAndSharePdf(
        context: Context,
        user: UserEntity,
        cycles: List<CycleEntity>,
        logs: List<LogEntity>
    ) {
        val pdfFile = generatePdfFile(context, user, cycles, logs)
        shareFile(context, pdfFile, "application/pdf", "LunaCycle Medical Health Report (PDF)")
    }

    fun downloadPdf(
        context: Context,
        user: UserEntity,
        cycles: List<CycleEntity>,
        logs: List<LogEntity>
    ) {
        val pdfFile = generatePdfFile(context, user, cycles, logs)
        saveFileToDownloads(context, pdfFile, "application/pdf", "LunaCycle_Medical_Report.pdf")
    }

    fun generateAndShareCsv(
        context: Context,
        user: UserEntity,
        cycles: List<CycleEntity>,
        logs: List<LogEntity>
    ) {
        val csvFile = generateCsvFile(context, user, cycles, logs)
        shareFile(context, csvFile, "text/csv", "LunaCycle Health Data (CSV)")
    }

    fun downloadCsv(
        context: Context,
        user: UserEntity,
        cycles: List<CycleEntity>,
        logs: List<LogEntity>
    ) {
        val csvFile = generateCsvFile(context, user, cycles, logs)
        saveFileToDownloads(context, csvFile, "text/csv", "LunaCycle_Health_Data.csv")
    }

    private fun saveFileToDownloads(context: Context, sourceFile: File, mimeType: String, fileName: String) {
        try {
            var destinationUri: android.net.Uri? = null
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = context.contentResolver
                destinationUri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (destinationUri != null) {
                    resolver.openOutputStream(destinationUri)?.use { out ->
                        sourceFile.inputStream().use { input ->
                            input.copyTo(out)
                        }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val targetFile = File(downloadsDir, fileName)
                sourceFile.copyTo(targetFile, overwrite = true)
                destinationUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", targetFile)
            }

            android.widget.Toast.makeText(context, "📥 Downloaded $fileName to Downloads folder", android.widget.Toast.LENGTH_LONG).show()

            val viewUri = destinationUri ?: FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", sourceFile)
            LunaNotificationHelper.sendDocumentDownloadedNotification(context, fileName, viewUri, mimeType)

            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(viewUri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(viewIntent)
            } catch (_: Exception) {}
        } catch (_: Exception) {
            android.widget.Toast.makeText(context, "Saved to app documents: $fileName", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, "Here is my cycle and symptom health export from LunaCycle.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Export $title").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
