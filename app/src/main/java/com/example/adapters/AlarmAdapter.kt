package com.example.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidmodule7broadcastalarm.databinding.ItemAlarmBinding
import com.room.entity.Alarm

class AlarmAdapter() : ListAdapter<Alarm, AlarmAdapter.ViewHolder>(MyDiffUtil()) {

    lateinit var itemClick: OnItemClickListener
    lateinit var itemLongClickLong: OnItemLongClickListener
    lateinit var alarmStart: OnAlarmStartClickListener

    fun interface OnItemClickListener {
        fun onClick(id: Int)
    }

    fun interface OnItemLongClickListener {
        fun onClick(id: Int)
    }


    fun interface OnAlarmStartClickListener {
        fun onClick(alarm: Alarm)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClick = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        itemLongClickLong = listener
    }

    fun setOnAlarmStartClickListener(listener: OnAlarmStartClickListener) {
        alarmStart = listener
    }

    inner class ViewHolder(var binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(alarm: Alarm) {
            val minute = "%02d".format(alarm.minute)
            binding.alarmTime.text = "${alarm.hour}:$minute"
            if (alarm.repeat == 1) {
                binding.alarmRepeat.text = "Daily"
            } else {
                binding.alarmRepeat.text = "Once"
            }
            binding.alarmSwitch.isChecked = alarm.start == 1
            binding.root.setOnClickListener {
                itemClick.onClick(alarm.id!!)
            }
            binding.alarmSwitch.setOnClickListener {
                alarmStart.onClick(alarm)
            }
            binding.root.setOnLongClickListener {
                itemLongClickLong.onClick(alarm.id!!)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAlarmBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class MyDiffUtil : DiffUtil.ItemCallback<Alarm>() {
        override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return when {
                oldItem.hour != newItem.hour -> {
                    false
                }
                oldItem.minute != newItem.minute -> {
                    false
                }
                oldItem.start != newItem.start -> {
                    false
                }
                oldItem.repeat != newItem.repeat -> {
                    false
                }
                else -> true
            }
        }

    }
}