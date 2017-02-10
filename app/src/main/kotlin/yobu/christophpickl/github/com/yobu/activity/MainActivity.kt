package yobu.christophpickl.github.com.yobu.activity

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import android.widget.TextView
import com.pawegio.kandroid.find
import com.pawegio.kandroid.runDelayed
import com.pawegio.kandroid.toast
import yobu.christophpickl.github.com.yobu.Answer
import yobu.christophpickl.github.com.yobu.BoRelevantMeridian
import yobu.christophpickl.github.com.yobu.Question
import yobu.christophpickl.github.com.yobu.R
import yobu.christophpickl.github.com.yobu.activity.view.AnswersListAdapter
import yobu.christophpickl.github.com.yobu.logic.QuestionsLoader
import yobu.christophpickl.github.com.yobu.logic.QuestionSelector
import yobu.christophpickl.github.com.yobu.logic.QuestionStatisticService
import yobu.christophpickl.github.com.yobu.logic.persistence.createPreferences
import yobu.christophpickl.github.com.yobu.common.LOG
import yobu.christophpickl.github.com.yobu.logic.BoPunctGenerator

class MainActivity : AppCompatActivity() {

    companion object {
        private val LOG = LOG(MainActivity::class.java)

    }

    private val stats by lazy { QuestionStatisticService(this) }
    private val prefs by lazy { createPreferences() }

    private val txtOutput by lazy { find<TextView>(R.id.txtOutput) }
    private val answersList by lazy {
        find<ListView>(R.id.answersList) .apply {
            setOnItemClickListener { parent, view, position, id ->
                LOG.d("answersList.onItemClickListener on position: $position")
                val answerLabel = view.find<TextView>(R.id.answerLabel)
                onAnswerClicked(currentQuestion!!, currentQuestion!!.answers[position], answerLabel)
            }
        }
    }
    private val txtCountRight by lazy { find<TextView>(R.id.txtCountRight) }

    private val boGenerator = BoPunctGenerator()

    private val questions by lazy {
        QuestionSelector(
                QuestionsLoader().load(resources.openRawResource(R.raw.questions_catalog))
                        .plus(boGenerator.generateDefaultQuestions()),
                stats)
    }

    private var currentQuestion: Question? = null
    private var currentHighScore = 0
    private var currentCountRight: Int = 0
        get() = field
        set(value) {
            txtCountRight.text = "$value / $currentHighScore"
            field = value
        }


    override fun onCreate(savedInstanceState: Bundle?) { // vs: onRestoreInstanceState
        LOG.i("onCreate(savedInstanceState.isNull=${savedInstanceState == null})")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentHighScore = prefs.highscore

        if (savedInstanceState != null) {
            val yobuState = StateManager.read(savedInstanceState)
            currentCountRight = yobuState.countRight
             changeQuestion(yobuState.question)
        } else {
            currentCountRight = 0 // force highscore number update
            onNextQuestion()
        }
    }

    override fun onStart() {
        LOG.i("onStart()")
        super.onStart()
    }

    override fun onResume() {
        LOG.i("onResume()")
        super.onResume()
    }

    override fun onPause() { // vs: onSaveInstanceState
        LOG.i("onPause()")
        super.onPause()
    }

    override fun onStop() {
        LOG.i("onStop()")
        super.onStop()
    }

    override fun onRestart() {
        LOG.i("onRestart()")
        super.onRestart()
    }

    override fun onDestroy() {
        LOG.i("onDestroy()")
        super.onDestroy()
    }

    override fun onSaveInstanceState(state: Bundle?) {
        LOG.i { "onSaveInstanceState(state.isNull=${state == null})" }
        super.onSaveInstanceState(state)
        if (state == null) {
            LOG.w("Not saving state as of null bundle!")
            return
        }
        StateManager.save(state, YobuState(
                countRight = currentCountRight,
                question = currentQuestion!!
        ))
    }

    override fun onRestoreInstanceState(state: Bundle?) {
        LOG.i { "onRestoreInstanceState(state.isNull=${state == null})" }
        super.onRestoreInstanceState(state)
    }

    private fun onNextQuestion() {
        LOG.d("onNextQuestion()")

        changeQuestion(questions.nextQuestion())
    }

    private fun changeQuestion(newQuestion: Question) {
        currentQuestion = newQuestion

        txtOutput.text = currentQuestion!!.text
        answersList.adapter = AnswersListAdapter(this, currentQuestion!!.answers)
    }

    private fun onAnswerClicked(question: Question, selectedAnswer: Answer, answerLabel: TextView) {
        answersList.isEnabled = false
        answerLabel.setBackgroundColor(if (selectedAnswer.isRight) Color.GREEN else Color.RED)

        if (selectedAnswer.isRight) {
            stats.rightAnswered(question)
            currentCountRight++
            if (currentCountRight - 1 == currentHighScore) {
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
        val newHighscore = currentCountRight
        if (newHighscore > oldHighscore) {
            toast("Juchu, neue Highscore: $newHighscore!")
            prefs.highscore = newHighscore
            currentHighScore = newHighscore
        }

        currentCountRight = 0
        onNextQuestion()
    }

}
