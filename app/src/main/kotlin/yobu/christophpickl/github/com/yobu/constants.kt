@file:Suppress("SimplifyBooleanWithConstants")

package yobu.christophpickl.github.com.yobu

import android.graphics.Color
import android.support.annotation.ColorInt
import android.view.View
import org.jetbrains.anko.backgroundColor

val GADSU_APP_VERSION = "0.2-alpha"

// FIXME add build time constants for development (disable during release); remove sequential questions; add "fast mode" in settings
private val ENABLE_DEVELOPMENT = false
val DISABLE_RANDOM_QUESTIONS = ENABLE_DEVELOPMENT && true
val ENABLE_FAST_MODE = ENABLE_DEVELOPMENT && true
val ENABLE_DEBUG_COLORS = ENABLE_DEVELOPMENT && true
fun View.debugColor(@ColorInt color: Int) {
    if (ENABLE_DEBUG_COLORS) {
        backgroundColor = color
    }
}

object Colors {
    val QuestionRight = Color.GREEN
    val QuestionWrong = Color.RED
}