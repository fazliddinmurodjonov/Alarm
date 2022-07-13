package com.example.androidmodule7broadcastalarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.os.Vibrator
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.App
import com.example.androidmodule7broadcastalarm.databinding.ActivitySecondBinding
import com.room.dao.AlarmDao
import com.room.database.AlarmDatabase
import com.room.entity.Alarm
import kotlinx.coroutines.processNextEventInCurrentThread
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class SecondActivity : AppCompatActivity(R.layout.activity_second) {
    private val binding: ActivitySecondBinding by viewBinding()
    private var calculatingHour = 0
    private var calculatingMinute = 0
    private var repeat = 0
    private var vibrate = 0
    var currentHour = 0
    var currentMinute = 0
    var selectedHour = 0
    var selectedMinute = 0
    var db = AlarmDatabase.getInstance(App.instance)
    private var status: Int = 0
    var time: Long = 0
    lateinit var alarmById: Alarm

    @SuppressLint("NewApi", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        status = intent.getIntExtra("status", 0)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        binding.datePicker.setIs24HourView(true)

        if (status != -1) {
            alarmById = db.alarmDao().getAlarmById(status)
            calculatingHour = alarmById.hourCalculate!!
            calculatingMinute = alarmById.minuteCalculate!!
            binding.datePicker.hour = alarmById.hour!!
            binding.datePicker.minute = alarmById.minute!!
            if (alarmById.repeat == 1) {
                binding.repeatSwitch.isChecked = true
                repeat = 1
            }
            if (alarmById.vibrate == 1) {
                binding.vibrateSwitch.isChecked = true
                vibrate = 1
            }
            loadDifference(alarmById.hour!!, alarmById.minute!!)
            binding.alarmCalculateTv.text = getCalculateTime(calculatingHour, calculatingMinute)
        } else {
            currentHour = binding.datePicker.hour
            currentMinute = binding.datePicker.minute
            loadDifference(currentHour, currentMinute)

            binding.alarmCalculateTv.text = getCalculateTime(calculatingHour, calculatingMinute)
        }

        binding.datePicker.setOnTimeChangedListener { p0, p1, p2 ->
            loadDifference(p1, p2)
        }
        binding.repeatSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.repeatStatus.text = "Daily"
                repeat = 1
            } else {
                binding.repeatStatus.text = "Once"
                repeat = 0
            }
        }
        binding.vibrateSwitch.setOnCheckedChangeListener { compoundButton, b ->
            vibrate = if (b) {
                1
            } else {
                0

            }
        }
        binding.cancelTv.setOnClickListener {
            finish()
        }
        binding.saveTv.setOnClickListener {
            alarmingClock()
        }


    }

    private fun loadDifference(p1: Int, p2: Int) {
        val date = Date()
        val hourFormat = SimpleDateFormat("HH")
        val minuteFormat = SimpleDateFormat("mm")
        val cHour = hourFormat.format(date).toInt()
        val cMinute = minuteFormat.format(date).toInt()
        currentHour = cHour
        currentMinute = cMinute
        selectedHour = p1
        selectedMinute = p2
        var difference =
            (currentHour * 60 + currentMinute) - (selectedHour * 60 + selectedMinute)
        if (difference > 0) {
            calculatingHour = (24 * 60 - difference) / 60
            calculatingMinute = (24 * 60 - difference) - calculatingHour * 60
            binding.alarmCalculateTv.text = getCalculateTime(calculatingHour, calculatingMinute)
        } else {
            calculatingHour = abs(difference) / 60
            calculatingMinute = abs(abs(difference) - 60 * calculatingHour)
            binding.alarmCalculateTv.text = getCalculateTime(calculatingHour, calculatingMinute)
        }
    }

    private fun alarmingClock() {
        if (status != -1) {
            loadAlarmManager(status)
            alarmById.repeat = repeat
            alarmById.vibrate = vibrate
            alarmById.hour = selectedHour
            alarmById.minute = selectedMinute
            alarmById.hourCalculate = calculatingHour
            alarmById.minuteCalculate = calculatingMinute
            db.alarmDao().updateAlarm(alarmById)
        } else {

            var alarm = Alarm(1,
                time,
                selectedHour,
                selectedMinute,
                calculatingHour,
                calculatingMinute,
                repeat,
                vibrate)
            db.alarmDao().insertAlarm(alarm)
            val alarmList = db.alarmDao().AllAlarm()
            loadAlarmManager(alarmList[alarmList.size - 1].id!!)
        }

        finish()
    }

    @SuppressLint("SimpleDateFormat", "UnspecifiedImmutableFlag")
    private fun loadAlarmManager(id: Int) {
        val date = Date()
        val secondFormat = SimpleDateFormat("ss")
        val format = secondFormat.format(date).toInt()
        val t = (60 * calculatingHour + calculatingMinute) * 60 * 1000
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val time = System.currentTimeMillis() + t - format * 1000
        this.time = time
        val intent = Intent(this, MyReceiver::class.java)
        intent.putExtra("alarmId", id)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (repeat == 1) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                time,
                AlarmManager.INTERVAL_DAY,
                pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        }
    }


    private fun getCalculateTime(hour: Int, minute: Int): String {
        return "Alarm in $hour hours $minute minutes"
    }


}