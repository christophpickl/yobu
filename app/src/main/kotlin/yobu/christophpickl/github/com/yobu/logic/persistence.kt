package yobu.christophpickl.github.com.yobu.logic

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences


fun Activity.createPreferences(): Preferences {
    val settings = getSharedPreferences("gadsu_prefs", Context.MODE_PRIVATE)
    return Preferences(settings)
}

class Preferences(private val settings: SharedPreferences) {

    companion object {
        private val KEY_HIGHSCORE_CORRECT = "highscore"
        private val LOG = yobu.christophpickl.github.com.yobu.misc.LOG(Preferences::class.java)
    }

    var highscore: Int
        get() = settings.getInt(KEY_HIGHSCORE_CORRECT, 0)
        set(value) {
            LOG.d { "Set value to: $value" }
            settings.edit().apply {
                putInt(KEY_HIGHSCORE_CORRECT, value)
                apply()
            }
        }
}
