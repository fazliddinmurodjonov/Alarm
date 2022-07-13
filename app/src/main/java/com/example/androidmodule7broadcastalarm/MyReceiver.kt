package com.example.androidmodule7broadcastalarm

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast


class MyReceiver : BroadcastReceiver() {

    @SuppressLint("NewApi")
    override fun onReceive(context: Context, intent: Intent) {
        var i = Intent(context, AlarmingActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val id = intent.getIntExtra("alarmId", 0)
        i.putExtra("alarmId", id)
        context.startActivity(i)
    }
}