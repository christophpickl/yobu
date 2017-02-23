package yobu.christophpickl.github.com.yobu.common

import android.app.AlertDialog
import android.content.Context
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.text.Html
import android.view.View
import android.widget.TextView
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

fun AlertDialog.Builder.setHtmlView(context: Context, htmlText: String): AlertDialog.Builder {
    val view = TextView(context)
    val span = SpannableString(htmlText)
    Linkify.addLinks(span, Linkify.WEB_URLS)
    view.text = span
    view.movementMethod = LinkMovementMethod.getInstance()
    setView(view)
    return this
}

var TextView.htmlText: String
    get() = throw PropertyWithoutGetterException("htmlText")
    set(value) {
        text = Html.fromHtml(value)
    }
