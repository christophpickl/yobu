package yobu.christophpickl.github.com.yobu.common

import android.util.Log


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
