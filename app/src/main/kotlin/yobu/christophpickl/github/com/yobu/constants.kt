@file:Suppress("SimplifyBooleanWithConstants")

package yobu.christophpickl.github.com.yobu

import android.support.annotation.ColorInt
import android.view.View
import org.jetbrains.anko.backgroundColor

val YOBU_APP_VERSION = "0.1-SNAPSHOT"

// FIXME change this variable during release build!
private val ENABLE_DEVELOPMENT = true

val DISABLE_RANDOM_QUESTIONS = ENABLE_DEVELOPMENT && true
val ENABLE_FAST_MODE = ENABLE_DEVELOPMENT && true
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