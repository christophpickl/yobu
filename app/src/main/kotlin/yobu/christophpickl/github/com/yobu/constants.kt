@file:Suppress("SimplifyBooleanWithConstants")

package yobu.christophpickl.github.com.yobu

import android.graphics.Color
import android.support.annotation.ColorInt
import android.view.View
import org.jetbrains.anko.backgroundColor

val GADSU_APP_VERSION = "0.1"


private val ENABLE_DEVELOPMENT = true
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