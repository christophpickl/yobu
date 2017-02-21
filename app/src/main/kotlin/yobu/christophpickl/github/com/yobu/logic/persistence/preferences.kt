package yobu.christophpickl.github.com.yobu.logic.persistence

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import yobu.christophpickl.github.com.yobu.common.edit


fun Activity.createPreferences(): Preferences {
    val settings = getSharedPreferences("gadsu_prefs", Context.MODE_PRIVATE)
    return Preferences(settings)
}

class Preferences(private val settings: SharedPreferences) {

    companion object {
        private val KEY_HIGHSCORE = "highscore"
        private val KEY_CURRENT_SCORE = "current_score"
        private val LOG = yobu.christophpickl.github.com.yobu.common.LOG(Preferences::class.java)
    }

    var highscore: Int
        get() = settings.getInt(KEY_HIGHSCORE, 0)
        set(value) {
            LOG.d { "Set highscore to: $value" }
            settings.edit {
                putInt(KEY_HIGHSCORE, value)
            }
        }
    var currentScore: Int
        get() = settings.getInt(KEY_CURRENT_SCORE, 0)
        set(value) {
            LOG.d { "Set currentScore to: $value" }
            settings.edit {
                putInt(KEY_CURRENT_SCORE, value)
            }
        }
}
