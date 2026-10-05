package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.ChatMessageEntity
import com.example.data.model.CycleEntity
import com.example.data.model.LogEntity
import com.example.data.model.StringListTypeConverter
import com.example.data.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CycleEntity::class,
        LogEntity::class,
        ChatMessageEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(StringListTypeConverter::class)
abstract class LunaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cycleDao(): CycleDao
    abstract fun logDao(): LogDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: LunaDatabase? = null

        fun getInstance(context: Context): LunaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LunaDatabase::class.java,
                    "lunacycle.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
