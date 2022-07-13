package com.room.dao

import androidx.room.*
import com.room.entity.Alarm
import io.reactivex.rxjava3.core.Flowable

@Dao
interface AlarmDao {
    @Insert
    fun insertAlarm(alarm: Alarm)

    @Delete
    fun deleteAlarm(alarm: Alarm)

    @Update
    fun updateAlarm(alarm: Alarm)

    @Query("select *from Alarm where id = :id")
    fun getAlarmById(id: Int): Alarm

    @Query("select *from Alarm")
    fun getAllAlarm(): Flowable<List<Alarm>>

    @Query("select *from Alarm")
    fun AllAlarm(): List<Alarm>
}