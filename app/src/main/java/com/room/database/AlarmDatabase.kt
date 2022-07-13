package com.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.room.dao.AlarmDao
import com.room.entity.Alarm

@Database(entities = [Alarm::class], version = 1)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object {
        private var instance: AlarmDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AlarmDatabase {
            if (instance == null) {
                instance =
                    Room.databaseBuilder(context,
                        AlarmDatabase::class.java,
                        "alarm_db")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
            }
            return instance!!
        }

    }
}