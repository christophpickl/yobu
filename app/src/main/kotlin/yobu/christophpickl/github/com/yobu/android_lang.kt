package yobu.christophpickl.github.com.yobu

import android.util.Log
import com.pawegio.kandroid.runOnUiThread
import java.util.*
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

fun runLaterOnUiThread(delayInMs: Long, delayedAction: () -> Unit) {
    Timer().schedule(object : TimerTask() {
        override fun run() {
            runOnUiThread { delayedAction() }
        }

    }, delayInMs)
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
        // TODO fix exception handling
        e.printStackTrace()
    } finally {
        try {
            this.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
    return sb.toString()
}