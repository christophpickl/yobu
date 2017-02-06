package yobu.christophpickl.github.com.yobu.activity

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
import yobu.christophpickl.github.com.yobu.Answer
import yobu.christophpickl.github.com.yobu.Question
import yobu.christophpickl.github.com.yobu.R
import yobu.christophpickl.github.com.yobu.logic.CatalogsRepository
import yobu.christophpickl.github.com.yobu.logic.QuestionRepo
import yobu.christophpickl.github.com.yobu.logic.persistence.createPreferences
import yobu.christophpickl.github.com.yobu.misc.LOG


class MainActivity : AppCompatActivity() {

    companion object {
        private val LOG = LOG(MainActivity::class.java)
    }

    private val prefs by lazy { createPreferences() }

    private val txtOutput by lazy { find<TextView>(R.id.txtOutput) }
    private val answersList by lazy { find<ListView>(R.id.answersList) }
    private val txtCountCorrect by lazy { find<TextView>(R.id.txtCountCorrect) }

    private val questions by lazy {
        QuestionRepo(CatalogsRepository().load(resources.openRawResource(R.raw.questions_catalog)))
    }

    private var currentHighScore = 0

    private var countCorrect: Int = 0
        get() = field
        set(value) {
            txtCountCorrect.text = "$value / $currentHighScore"
            field = value
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.i("onCreate(..)")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentHighScore = prefs.highscore
        countCorrect = 0 // force highscore number update

        onNextQuestion()
    }

    private fun onNextQuestion() {
        LOG.d("onNextQuestion()")

        val question = questions.nextQuestion()
        txtOutput.text = question.text

        answersList.adapter = AnswersListAdapter(this, question.answers)
        answersList.setOnItemClickListener { parent, view, position, id ->
            LOG.d("onItemClickListener on position: $position")
            val answerLabel = view.find<TextView>(R.id.answerLabel)
            onAnswerClicked(question, question.answers[position], answerLabel)
        }
    }

    private fun onAnswerClicked(question: Question, selectedAnswer: Answer, answerLabel: TextView) {
        answersList.isEnabled = false
        answerLabel.setBackgroundColor(if (selectedAnswer.isCorrect) Color.GREEN else Color.RED)

        if (selectedAnswer.isCorrect) {
            countCorrect++
            if (countCorrect - 1 == currentHighScore) {
                toast("Highscore gebrochen!")
            }
        } else {
            val correctAnswerView = answersList.getChildAt(question.indexOfCorrectAnswer).find<TextView>(R.id.answerLabel)
            correctAnswerView.setBackgroundColor(Color.GREEN)
        }

        runDelayed(if(selectedAnswer.isCorrect) 500 else 2000) {
            answersList.isEnabled = true
            if (selectedAnswer.isCorrect) {
                onNextQuestion()
            } else {
                onRestartRiddle()
            }
        }
    }

    private fun onRestartRiddle() {
        val oldHighscore = prefs.highscore
        val newHighscore = countCorrect
        if (newHighscore > oldHighscore) {
            toast("Juchu, neue Highscore: $newHighscore!")
            prefs.highscore = newHighscore
            currentHighScore = newHighscore
        }

        countCorrect = 0
        onNextQuestion()
    }

}


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
