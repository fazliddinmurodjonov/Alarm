package com.example.androidmodule7broadcastalarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.App
import com.example.adapters.AlarmAdapter
import com.example.androidmodule7broadcastalarm.databinding.ActivityMainBinding
import com.example.androidmodule7broadcastalarm.databinding.DialogDeleteBinding
import com.room.database.AlarmDatabase
import com.room.entity.Alarm
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.function.Consumer

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val binding: ActivityMainBinding by viewBinding()
    var db = AlarmDatabase.getInstance(App.instance)
    var alarmAdapter = AlarmAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db.alarmDao().getAllAlarm()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                @SuppressLint("NewApi")
                object : Consumer<List<Alarm>>,
                    io.reactivex.rxjava3.functions.Consumer<List<Alarm>> {
                    override fun accept(t: List<Alarm>) {
                        alarmAdapter.submitList(t)
                    }
                },

                @SuppressLint("NewApi")
                object : Consumer<Throwable>,
                    io.reactivex.rxjava3.functions.Consumer<Throwable> {
                    override fun accept(t: Throwable) {
                    }
                })

        binding.alarmRV.adapter = alarmAdapter

        alarmAdapter.setOnItemClickListener { id ->
            changeActivity(id)
        }
        alarmAdapter.setOnItemLongClickListener { id ->
            val dialog = Dialog(this)
            val dialogView = DialogDeleteBinding.inflate(LayoutInflater.from(this), null, false)
            dialog.setContentView(dialogView.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogView.noBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialogView.yesBtn.setOnClickListener {
                val alarm = db.alarmDao().getAlarmById(id)
                alarmManagerCheck(0, alarm.time!!, alarm.repeat!!, alarm.id!!)
                db.alarmDao().deleteAlarm(alarm)
                dialog.dismiss()
            }
            dialog.show()
        }
        alarmAdapter.setOnAlarmStartClickListener { alarm ->
            if (alarm.start == 1) {
                alarm.start = 0
                alarmManagerCheck(0, alarm.time!!, alarm.repeat!!, alarm.id!!)
                db.alarmDao().updateAlarm(alarm)
            } else {
                alarm.start = 1
                alarmManagerCheck(1, alarm.time!!, alarm.repeat!!, alarm.id!!)
                db.alarmDao().updateAlarm(alarm)
            }
        }

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        binding.add.setOnClickListener {
            changeActivity(-1)
        }


    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun alarmManagerCheck(check: Int, time: Long, repeat: Int, id: Int) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyReceiver::class.java)
        intent.putExtra("alarmId", id)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (repeat == 1) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                time, AlarmManager.INTERVAL_DAY, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        }
        if (check == 0) {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun changeActivity(id: Int) {
        val intent = Intent(this, SecondActivity::class.java)
        intent.putExtra("status", id)
        startActivity(intent)
    }
}