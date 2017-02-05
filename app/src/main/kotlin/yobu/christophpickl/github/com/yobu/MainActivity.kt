package yobu.christophpickl.github.com.yobu

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.pawegio.kandroid.find
import com.pawegio.kandroid.inflateLayout
import com.pawegio.kandroid.runDelayed
import com.pawegio.kandroid.toast

class MainActivity : AppCompatActivity() {

    companion object {
        private val LOG = LOG(MainActivity::class.java)
    }

    private val txtOutput by lazy { find<TextView>(R.id.txtOutput) }
    private val btnStartRiddle by lazy { find<Button>(R.id.btnStartRiddle) }
    private val answersList by lazy { find<ListView>(R.id.answersList) }
    private val txtCountCorrect by lazy { find<TextView>(R.id.txtCountCorrect) }

    private val questions = QuestionRepo()

    private var countCorrect: Int = 0
        get() = field
        set(value) {
            if (field != value) {
                txtCountCorrect.text = value.toString()
            }
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.i("onCreate(..)")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartRiddle.setOnClickListener {
            (btnStartRiddle.parent as LinearLayout).removeView(btnStartRiddle)
            onNextQuestion()
        }
    }

    private fun onNextQuestion() {
        LOG.d("onNextQuestion()")

        val question = questions.nextQuestion()
        txtOutput.text = question.text

        answersList.adapter = AnswersListAdapter(this, question.answers)
        answersList.setOnItemClickListener { parent, view, position, id ->
            LOG.d("onItemClickListener on position: $position")
            val answerLabel = view.find<TextView>(R.id.answerLabel)
            onAnswerClicked(question.answers[position], answerLabel)
        }
    }

    private fun onAnswerClicked(answer: Answer, answerLabel: TextView) {
        answersList.isEnabled = false
        answerLabel.setBackgroundColor(if (answer.isCorrect) Color.GREEN else Color.RED)

        if (answer.isCorrect) {
            countCorrect++
        } else {
            toast("Flasche Antwort!")
        }

        runDelayed(500) {
            answersList.isEnabled = true
            if (answer.isCorrect) {
                onNextQuestion()
            } else {
                onRestartRiddle()
            }
        }
    }

    private fun onRestartRiddle() {
        countCorrect = 0
        onNextQuestion()
    }

}


class AnswersListAdapter(
        context: Context,
        private val answers: List<Answer>)
    : ArrayAdapter<Answer>(context, R.layout.list_answer, answers.toTypedArray()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rawView = context.inflateLayout(R.layout.list_answer)
        val label = rawView.find<TextView>(R.id.answerLabel)
        val answer = answers[position]
        label.text = answer.text
        return rawView
    }

}
