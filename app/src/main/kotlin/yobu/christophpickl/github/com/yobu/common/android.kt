package yobu.christophpickl.github.com.yobu.common

import android.view.View

fun View.onClickMakeGone(additionalAction: () -> Unit = { }) {
    setOnClickListener {
        visibility = View.GONE
        additionalAction()
    }
}

fun View.toggleVisibility() {
    visibility = if (visibility == View.GONE) View.VISIBLE else View.GONE
}
