@file:Suppress("SimplifyBooleanWithConstants")

package yobu.christophpickl.github.com.yobu

import android.support.annotation.ColorInt
import android.view.View
import org.jetbrains.anko.backgroundColor

val YOBU_APP_VERSION = "0.2-alpha"

// FIXME add build time constants for development (disable during release); remove sequential questions; add "fast mode" in settings
private val ENABLE_DEVELOPMENT = false

val ENABLE_DEBUG_COLORS = ENABLE_DEVELOPMENT && true
fun View.debugColor(color: MyColor) {
    if (ENABLE_DEBUG_COLORS) {
        backgroundColor = color.intVal
    }
}

enum class MyColor(
        @ColorInt val intVal: Int
) {
    QuestionRight(0xFF00FF00.toInt()),
    QuestionWrong(0xFFFF0000.toInt())
    ;

    // e.g.: "#00FF00"
    val hexStringVal = String.format("#%06X", (0xFFFFFF and intVal))

}