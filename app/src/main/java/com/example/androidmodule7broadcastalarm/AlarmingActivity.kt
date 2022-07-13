package com.example.androidmodule7broadcastalarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Vibrator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.App
import com.example.androidmodule7broadcastalarm.databinding.ActivityAlarmingBinding
import com.room.database.AlarmDatabase
import com.room.entity.Alarm
import java.text.SimpleDateFormat
import java.util.*

class AlarmingActivity : AppCompatActivity(R.layout.activity_alarming) {
    private val binding: ActivityAlarmingBinding by viewBinding()
    var db = AlarmDatabase.getInstance(App.instance)
    lateinit var mp: MediaPlayer
    lateinit var v: Vibrator
    lateinit var alarm: Alarm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val alarmId = intent.getIntExtra("alarmId", 0)
        alarm = db.alarmDao().getAlarmById(alarmId)

        val hour = "%02d".format(alarm.hour)
        val minute = "%02d".format(alarm.minute)
        binding.alarmTime.text = "$hour:$minute"
        loadData()
        binding.btn.setOnClickListener {
            loadAlarmManager()
            mp.stop()
            if (alarm.vibrate == 1) {
                v.cancel()
            }
            finish()
        }
    }

    private fun loadData() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        mp = MediaPlayer.create(this, R.raw.alarm_clock);
        mp.start()
        mp.isLooping = true
        if (alarm.vibrate == 1) {
            v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val pattern = longArrayOf(0, 300, 600)
            v.vibrate(pattern, 0)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun loadAlarmManager() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (alarm.repeat == 1) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                alarm.time!!,
                AlarmManager.INTERVAL_DAY,
                pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.time!!, pendingIntent)
            alarmManager.cancel(pendingIntent)
            alarm.start = 0
            db.alarmDao().updateAlarm(alarm)
        }
    }
}