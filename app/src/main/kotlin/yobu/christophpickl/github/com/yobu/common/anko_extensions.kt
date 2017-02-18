package yobu.christophpickl.github.com.yobu.common

import android.graphics.Color
import android.support.annotation.ColorInt
import android.view.ViewManager
import org.jetbrains.anko.textColor
import org.jetbrains.anko.textView


fun ViewManager.textViewX(
        label: String = "",
        @ColorInt color: Int = Color.BLACK,
        size: Float = 20f,
        programmaticId: Int? = null
): android.widget.TextView {
    return textView {
        text = label
        textSize = size
        textColor = color
        programmaticId?.run { id = programmaticId }
    }
}