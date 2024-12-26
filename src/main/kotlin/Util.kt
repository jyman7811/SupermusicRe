package org.example

import com.fasterxml.jackson.databind.deser.DataFormatReaders.Match
import net.dv8tion.jda.api.entities.emoji.Emoji
import java.awt.Color
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

object Util {
    private val patt: Pattern = Pattern.compile("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")

    fun URLMatch(arg: String) : Boolean {
        try {
            val matcher = patt.matcher(arg)
            return matcher.matches();
        } catch (_: Exception) {
            return false;
        }
    }

    fun numberToEmoji(num: Int) : Emoji {
        return when (num) {
            1 -> Emoji.fromUnicode("\uE21C")
            2 -> Emoji.fromUnicode("\uE21D")
            3 -> Emoji.fromUnicode("\uE21E")
            4 -> Emoji.fromUnicode("\uE21F")
            5 -> Emoji.fromUnicode("\uE220")
            else -> Emoji.fromUnicode("\uE21C")
        }
    }

    /**
     * This returns random color in java.awt.Color.
     * @return Returns random color.
     */
    fun getRandomColor(): Color {
        val random = Random()
        return Color(random.nextInt(255), random.nextInt(255), random.nextInt(255))
    }

    /**
     * Converts milliseconds to a formatted string.
     * @param ms Time in milliseconds. ex) uptime
     * @return Returns a formatted string.
     */
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