package yobu.christophpickl.github.com.yobu.common

import android.text.Html
import android.view.View
import android.widget.TextView
import org.jetbrains.anko.PropertyWithoutGetterException

fun View.onClickMakeGone(additionalAction: () -> Unit = { }) {
    setOnClickListener {
        visibility = View.GONE
        additionalAction()
    }
}

fun View.toggleVisibility() {
    visibility = if (visibility == View.GONE) View.VISIBLE else View.GONE
}

var TextView.htmlText: String
    get() = throw PropertyWithoutGetterException("htmlText")
    set(value) {
        text = Html.fromHtml(value)
    }
