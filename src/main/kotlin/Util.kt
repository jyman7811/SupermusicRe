package org.example

import java.awt.Color
import java.util.*
import java.util.concurrent.TimeUnit

object Util {

    fun getRandomColor(): Color {
        val random = Random()
        return Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))
    }

    fun convertTime(ms: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(ms).toInt() % 24
        val minutes = (TimeUnit.MILLISECONDS.toMinutes(ms) % 60).toInt()
        val seconds = (TimeUnit.MILLISECONDS.toSeconds(ms) % 60 % 60).toInt()
        var str = ""
        if (hours != 0) {
            str += "${hours}시간 "
        }
        if (minutes != 0) {
            str += "${minutes}분 "
        }
        if (seconds != 0) {
            str +="${seconds}초"
        }
        return str
    }

}