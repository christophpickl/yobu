package yobu.christophpickl.github.com.yobu.common

import android.graphics.Color
import android.support.annotation.ColorInt
import android.view.View
import android.view.ViewManager
import android.widget.RelativeLayout
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

// fix lparams ambiguity: https://github.com/Kotlin/anko/issues/275
private val defaultInit: Any.() -> Unit = {}
fun <T: View> T.lparams_rl(
        width: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        height: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        init: RelativeLayout.LayoutParams.() -> Unit = defaultInit
): T {
    val layoutParams = RelativeLayout.LayoutParams(width, height)
    layoutParams.init()
    this@lparams_rl.layoutParams = layoutParams
    return this
}
