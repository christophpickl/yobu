package yobu.christophpickl.github.com.yobu.logic.persistence

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences


fun Activity.createPreferences(): Preferences {
    val settings = getSharedPreferences("gadsu_prefs", Context.MODE_PRIVATE)
    return Preferences(settings)
}

class Preferences(private val settings: SharedPreferences) {

    companion object {
        private val KEY_HIGHSCORE = "highscore"
        private val LOG = yobu.christophpickl.github.com.yobu.misc.LOG(Preferences::class.java)
    }

    var highscore: Int
        get() = settings.getInt(KEY_HIGHSCORE, 0)
        set(value) {
            LOG.d { "Set value to: $value" }
            settings.edit().apply {
                putInt(KEY_HIGHSCORE, value)
                apply()
            }
        }
}
