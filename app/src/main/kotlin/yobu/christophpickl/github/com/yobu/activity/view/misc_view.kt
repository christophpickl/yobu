package yobu.christophpickl.github.com.yobu.activity.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.pawegio.kandroid.find
import com.pawegio.kandroid.inflateLayout
import yobu.christophpickl.github.com.yobu.Answer
import yobu.christophpickl.github.com.yobu.R


class AnswersListAdapter(
        context: Context,
        private val answers: List<Answer>
)
    : ArrayAdapter<Answer>(context, R.layout.list_answer, answers.toTypedArray()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rawView = context.inflateLayout(R.layout.list_answer)
        val label = rawView.find<TextView>(R.id.answerLabel)
        val answer = answers[position]
        label.text = answer.text
        return rawView
    }

}
