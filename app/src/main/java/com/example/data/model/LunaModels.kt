package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "User",
    val age: Int = 25,
    val cycleLength: Int = 28,
    val periodLength: Int = 5,
    val isIrregular: Boolean = false,
    val profilePic: String? = null,
    val darkMode: Boolean = false,
    val notificationPeriodReminderDays: Int = 2,
    val notificationPeriodEnabled: Boolean = true,
    val notificationFertileEnabled: Boolean = true,
    val notificationDailyLogEnabled: Boolean = true,
    val waterGoalMl: Int = 2000
)

@Entity(tableName = "cycles")
data class CycleEntity(
    @PrimaryKey val id: String,
    val startDate: String, // YYYY-MM-DD
    val endDate: String? = null, // YYYY-MM-DD
    val length: Int = 0 // duration until next period start
)

@Entity(tableName = "logs")
data class LogEntity(
    @PrimaryKey val id: String,
    val date: String, // YYYY-MM-DD
    val symptoms: List<String> = emptyList(),
    val mood: String = "Neutral",
    val intensity: String = "None", // None, Light, Medium, Heavy
    val isPeriod: Boolean = false,
    val isPeriodEnd: Boolean = false,
    val notes: String = "",
    val waterMl: Int = 0,
    val bbt: Float? = null,
    val cervicalMucus: String? = null // Dry, Sticky, Creamy, Watery, Egg-white
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class StringListTypeConverter {
    private val moshi = Moshi.Builder().build()
    private val listType = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(listType)

    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return try {
            adapter.fromJson(value) ?: emptyList()
        } catch (_: Exception) {
            value.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }
    }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return adapter.toJson(list ?: emptyList())
    }
}
