package yobu.christophpickl.github.com.yobu.misc

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


fun LOG(javaClass: Class<out Any>) = Log2(javaClass)

class Log2(javaClass: Class<out Any>) {
    private val tag = javaClass.simpleName

    fun d(lazyMessage: () -> String) {
        if (Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, lazyMessage())
        }
    }

    fun d(message: String) {
        Log.d(tag, message)
    }

    fun i(message: String) {
        Log.i(tag, message)
    }

    fun w(message: String) {
        Log.w(tag, message)
    }

    fun e(message: String) {
        Log.e(tag, message)
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
