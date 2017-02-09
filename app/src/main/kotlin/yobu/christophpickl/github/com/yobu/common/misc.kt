package yobu.christophpickl.github.com.yobu.common

import android.content.SharedPreferences
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


// don't forget to call 'apply()' ;)
inline fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.func()
    editor.apply()
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
