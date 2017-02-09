package yobu.christophpickl.github.com.yobu.common

import java.text.SimpleDateFormat
import java.util.*

interface Clock {
    fun now(): Date
}

class RealClock : Clock {
    override fun now() = Date()

}


private val FORMAT = "yyyy-MM-dd HH:mm:ss"
private val FORMATTER = SimpleDateFormat(FORMAT, Locale.GERMAN)
fun Date.formatDateTime() = FORMATTER.format(this)!!
fun String.parseDateTime() = FORMATTER.parse(this)!!


