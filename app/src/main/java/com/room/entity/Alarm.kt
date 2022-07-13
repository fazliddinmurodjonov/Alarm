package com.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Alarm {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    var start: Int? = null
    var time: Long? = null
    var hour: Int? = null
    var minute: Int? = null
    var hourCalculate: Int? = null
    var minuteCalculate: Int? = null
    var repeat: Int? = null
    var vibrate: Int? = null

    constructor()
    constructor(
        start: Int?,
        time: Long?,
        hour: Int?,
        minute: Int?,
        hourCalculate: Int?,
        minuteCalculate: Int?,
        repeat: Int?,
        vibrate: Int?,
    ) {
        this.start = start
        this.time = time
        this.hour = hour
        this.minute = minute
        this.hourCalculate = hourCalculate
        this.minuteCalculate = minuteCalculate
        this.repeat = repeat
        this.vibrate = vibrate
    }


}