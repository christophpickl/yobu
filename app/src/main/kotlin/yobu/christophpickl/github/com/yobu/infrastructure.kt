package yobu.christophpickl.github.com.yobu

import android.util.Log
import com.pawegio.kandroid.runOnUiThread
import java.util.*

fun LOG(javaClass: Class<Any>) = Log2(javaClass)

class Log2(javaClass: Class<Any>) {
    private val tag = javaClass.simpleName
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