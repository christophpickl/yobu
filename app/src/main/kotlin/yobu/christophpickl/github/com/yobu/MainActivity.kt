package yobu.christophpickl.github.com.yobu

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.pawegio.kandroid.find
import com.pawegio.kandroid.inflateLayout
import com.pawegio.kandroid.runOnUiThread
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val LOG = LOG(javaClass)
    }

    private val txtOutput by lazy { find<TextView>(R.id.txtOutput) }
    private val btnStartRiddle by lazy { find<Button>(R.id.btnStartRiddle) }
    private val answersList by lazy { find<ListView>(R.id.answersList) }

    private val questions = QuestionRepo()

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
        answerLabel.setBackgroundColor(if (answer.isCorrect) Color.GREEN else Color.RED)
        runLaterOnUiThread(500) {
            onNextQuestion()
        }
    }

}

fun runLaterOnUiThread(delayInMs: Long, delayedAction: () -> Unit) {
    Timer().schedule(object : TimerTask() {
        override fun run() {
            runOnUiThread { delayedAction() }
        }

    }, delayInMs)
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
