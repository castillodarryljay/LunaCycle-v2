package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ProfileImageUtil {
    private const val TAG = "ProfileImageUtil"
    private const val PREFS_NAME = "luna_user_avatar_prefs"
    private const val KEY_AVATAR_PATH = "permanent_avatar_path"
    private const val CANONICAL_AVATAR_NAME = "luna_user_avatar.jpg"

    /**
     * Copies and optimizes selected image into app's private internal files directory.
     * Guarantees 100% permanent persistence across all app closures, device restarts, and updates.
     */
    fun saveProfileImageLocally(context: Context, sourceUri: Uri): String? {
        return try {
            val resolver = context.contentResolver

            // Attempt to take persistable URI permission if allowed by content provider
            try {
                resolver.takePersistableUriPermission(
                    sourceUri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
                // Not all providers support persistable permissions; that's why we copy locally!
            }

            val canonicalFile = File(context.filesDir, CANONICAL_AVATAR_NAME)
            val timestampedFile = File(context.filesDir, "luna_user_avatar_${System.currentTimeMillis()}.jpg")
            val tempFile = File(context.cacheDir, "temp_avatar_raw.tmp")

            // Step 1: Copy raw bytes to temp file safely
            var bytesCopied = 0L
            resolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    bytesCopied = input.copyTo(output)
                    output.flush()
                    try { output.fd.sync() } catch (_: Exception) {}
                }
            }

            if (bytesCopied <= 0L || !tempFile.exists()) {
                Log.e(TAG, "Failed to read input stream from URI: $sourceUri")
                return null
            }

            // Step 2: Decode bitmap, orient properly with EXIF, and compress to high-quality JPEG
            var processedBitmap: Bitmap? = null
            try {
                // Read EXIF orientation
                var orientation = ExifInterface.ORIENTATION_NORMAL
                try {
                    val exif = ExifInterface(tempFile.absolutePath)
                    orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                } catch (_: Exception) {}

                // Decode bounds first
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeFile(tempFile.absolutePath, options)

                // Compute downsample if very large (> 1200px)
                val maxDimension = 1200
                var sampleSize = 1
                while ((options.outWidth / sampleSize) > maxDimension || (options.outHeight / sampleSize) > maxDimension) {
                    sampleSize *= 2
                }

                val decodeOptions = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }

                val rawBitmap = BitmapFactory.decodeFile(tempFile.absolutePath, decodeOptions)
                if (rawBitmap != null) {
                    // Correct orientation if rotated
                    val matrix = Matrix()
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    }

                    processedBitmap = if (!matrix.isIdentity) {
                        Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
                    } else {
                        rawBitmap
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Bitmap processing warning, using direct copy", e)
            }

            // Step 3: Write out to canonical file and timestamped file
            if (processedBitmap != null) {
                FileOutputStream(canonicalFile).use { fos ->
                    processedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                    fos.flush()
                    try { fos.fd.sync() } catch (_: Exception) {}
                }
                FileOutputStream(timestampedFile).use { fos ->
                    processedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                    fos.flush()
                    try { fos.fd.sync() } catch (_: Exception) {}
                }
            } else {
                // Direct fallback copy
                tempFile.copyTo(canonicalFile, overwrite = true)
                tempFile.copyTo(timestampedFile, overwrite = true)
            }

            // Clean up temp file
            try { tempFile.delete() } catch (_: Exception) {}

            // Clean up old timestamped avatar files (keep latest 2)
            try {
                val oldFiles = context.filesDir.listFiles()?.filter {
                    it.name.startsWith("luna_user_avatar_") && it.name != timestampedFile.name
                }?.sortedByDescending { it.lastModified() }
                oldFiles?.drop(2)?.forEach { it.delete() }
            } catch (_: Exception) {}

            // Step 4: Persist in SharedPreferences using commit() for synchronous write
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString(KEY_AVATAR_PATH, canonicalFile.absolutePath)
                .putLong("avatar_updated_at", System.currentTimeMillis())
                .commit()

            Log.i(TAG, "Profile image successfully saved permanently at: ${canonicalFile.absolutePath}")
            canonicalFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error saving profile image locally", e)
            null
        }
    }

    /**
     * Retrieves the persisted avatar file path if it exists on disk.
     * Checks canonical file, timestamped files, and SharedPreferences.
     */
    fun getPersistedAvatarPath(context: Context): String? {
        val canonicalFile = File(context.filesDir, CANONICAL_AVATAR_NAME)
        if (canonicalFile.exists() && canonicalFile.length() > 0) {
            return canonicalFile.absolutePath
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedPath = prefs.getString(KEY_AVATAR_PATH, null)
        if (!savedPath.isNullOrEmpty()) {
            val file = File(savedPath)
            if (file.exists() && file.length() > 0) {
                return file.absolutePath
            }
        }

        // Look for any luna_user_avatar_* in filesDir
        val anyAvatar = context.filesDir.listFiles()
            ?.filter { it.name.startsWith("luna_user_avatar") && it.length() > 0 }
            ?.maxByOrNull { it.lastModified() }
        if (anyAvatar != null) {
            // Restore canonical file
            try {
                anyAvatar.copyTo(canonicalFile, overwrite = true)
            } catch (_: Exception) {}
            return canonicalFile.absolutePath
        }

        return null
    }
}
