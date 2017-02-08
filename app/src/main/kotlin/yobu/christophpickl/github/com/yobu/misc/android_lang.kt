package yobu.christophpickl.github.com.yobu.misc

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

interface Clock {
    fun now(): Date
}

class RealClock : Clock {
    override fun now() = Date()

}


// DATE
private val FORMAT = "yyyy-MM-dd HH:mm:ss"
private val FORMATTER = SimpleDateFormat(FORMAT, Locale.GERMAN)
fun Date.formatDateTime() = FORMATTER.format(this)
fun String.parseDateTime() = FORMATTER.parse(this)


fun LOG(javaClass: Class<out Any>) = Log2(javaClass)

class Log2(javaClass: Class<out Any>) {

    companion object {
        private val TAG = "yobu" // android limits tag to 23 chars
    }

    private val classNamePrefix = javaClass.simpleName + " - "

    fun v(lazyMessage: () -> String) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, classNamePrefix + lazyMessage())
        }
    }

    fun v(message: String) {
        v { message }
    }

    fun d(lazyMessage: () -> String) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, classNamePrefix + lazyMessage())
        }
    }

    fun d(message: String) {
        d { message }
    }

    fun i(lazyMessage: () -> String) {
        if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, classNamePrefix + lazyMessage())
        }
    }

    fun i(message: String) {
        i { message }
    }

    fun w(lazyMessage: () -> String) {
        if (Log.isLoggable(TAG, Log.WARN)) {
            Log.w(TAG, classNamePrefix + lazyMessage())
        }
    }

    fun w(message: String) {
        w { message }
    }


    fun e(lazyMessage: () -> String) {
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, classNamePrefix + lazyMessage())
        }
    }

    fun e(message: String) {
        e { message }
    }

}

fun InputStream.readString(): String {
    val reader = BufferedReader(InputStreamReader(this))
    val sb = StringBuilder()

    var line: String? = reader.readLine()
    try {
        while (line != null) {
            sb.append(line).append('\n')
            line = reader.readLine()
        }
    } catch (e: IOException) {
        throw RuntimeException("Failed to read from input stream!", e)
    } finally {
        try {
            this.close()
        } catch (e: IOException) {
            throw RuntimeException("Failed to close input stream!", e)
        }

    }
    return sb.toString()
}

fun <K, V> List<V>.associateMultiBy(transform: (V) -> K): Map<K, List<V>> {
    val map = mutableMapOf<K, MutableList<V>>()
    this.forEach { value ->
        val key = transform(value)
        if (!map.containsKey(key)) {
            map.put(key, mutableListOf())
        }
        map[key]!! += value
    }
    return map
}
