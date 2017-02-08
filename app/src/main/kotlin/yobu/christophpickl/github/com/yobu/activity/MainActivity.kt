package yobu.christophpickl.github.com.yobu.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import android.widget.TextView
import com.pawegio.kandroid.find
import com.pawegio.kandroid.runDelayed
import com.pawegio.kandroid.toast
import yobu.christophpickl.github.com.yobu.Answer
import yobu.christophpickl.github.com.yobu.Question
import yobu.christophpickl.github.com.yobu.R
import yobu.christophpickl.github.com.yobu.activity.view.AnswersListAdapter
import yobu.christophpickl.github.com.yobu.logic.CatalogsRepository
import yobu.christophpickl.github.com.yobu.logic.QuestionRepo
import yobu.christophpickl.github.com.yobu.logic.QuestionStatisticService
import yobu.christophpickl.github.com.yobu.logic.persistence.createPreferences
import yobu.christophpickl.github.com.yobu.misc.LOG

class MainActivity : AppCompatActivity() {

    companion object {
        private val LOG = LOG(MainActivity::class.java)
    }

    private val stats by lazy { QuestionStatisticService(this) }
    private val prefs by lazy { createPreferences() }

    private val txtOutput by lazy { find<TextView>(R.id.txtOutput) }
    private val answersList by lazy { find<ListView>(R.id.answersList) }
    private val txtCountRight by lazy { find<TextView>(R.id.txtCountRight) }

    private val questions by lazy {
        QuestionRepo(
                CatalogsRepository().load(resources.openRawResource(R.raw.questions_catalog)),
                stats)
    }

    private var currentHighScore = 0

    private var countRight: Int = 0
        get() = field
        set(value) {
            txtCountRight.text = "$value / $currentHighScore"
            field = value
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.i("onCreate(..)")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentHighScore = prefs.highscore
        countRight = 0 // force highscore number update

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
        answerLabel.setBackgroundColor(if (selectedAnswer.isRight) Color.GREEN else Color.RED)

        if (selectedAnswer.isRight) {
            stats.rightAnswered(question)
            countRight++
            if (countRight - 1 == currentHighScore) {
                toast("Highscore gebrochen!")
            }
        } else {
            stats.wrongAnswered(question)
            val rightAnswerView = answersList.getChildAt(question.indexOfRightAnswer).find<TextView>(R.id.answerLabel)
            rightAnswerView.setBackgroundColor(Color.GREEN)
        }

        runDelayed(if (selectedAnswer.isRight) 500 else 2000) {
            answersList.isEnabled = true
            if (selectedAnswer.isRight) {
                onNextQuestion()
            } else {
                onRestartRiddle()
            }
        }
    }

    private fun onRestartRiddle() {
        val oldHighscore = prefs.highscore
        val newHighscore = countRight
        if (newHighscore > oldHighscore) {
            toast("Juchu, neue Highscore: $newHighscore!")
            prefs.highscore = newHighscore
            currentHighScore = newHighscore
        }

        countRight = 0
        onNextQuestion()
    }

}
