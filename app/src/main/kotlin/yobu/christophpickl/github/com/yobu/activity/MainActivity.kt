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
import yobu.christophpickl.github.com.yobu.*
import yobu.christophpickl.github.com.yobu.activity.view.AnswersListAdapter
import yobu.christophpickl.github.com.yobu.logic.QuestionsLoader
import yobu.christophpickl.github.com.yobu.logic.QuestionSelector
import yobu.christophpickl.github.com.yobu.logic.QuestionStatisticService
import yobu.christophpickl.github.com.yobu.logic.persistence.createPreferences
import yobu.christophpickl.github.com.yobu.common.LOG
import yobu.christophpickl.github.com.yobu.logic.BoPunctGenerator
import android.R.menu
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import yobu.christophpickl.github.com.yobu.common.Alerts
import yobu.christophpickl.github.com.yobu.common.onClickMakeGone
import yobu.christophpickl.github.com.yobu.common.toggleVisibility


class MainActivity : AppCompatActivity() {

    companion object {
        private val LOG = LOG(MainActivity::class.java)

        private val ANSWER_DELAY_RIGHT = if (ENABLE_FAST_MODE) 100L else 500L
        private val ANSWER_DELAY_WRONG = if (ENABLE_FAST_MODE) 100L else 2000L
    }

    private val stats by lazy { QuestionStatisticService(this) }
    private val prefs by lazy { createPreferences() }

    private val cheatsheetContainer by lazy {
        find<RelativeLayout>(R.id.cheatsheet_container).apply {
            onClickMakeGone() {
                shownCheatsheet = ShownCheatsheet.NONE
            }
        }
    }
    private val cheatsheetImage by lazy {
        find<ImageView>(R.id.cheatsheet_image)
    }

    private val txtOutput by lazy { find<TextView>(R.id.txtOutput) }

    private val answersList by lazy {
        find<ListView>(R.id.answersList).apply {
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
                QuestionsLoader().load()
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
            toast("Ups, das war falsch.")
            question.indicesOfRightAnswers.forEach { index ->
                val rightAnswerView = answersList.getChildAt(index).find<TextView>(R.id.answerLabel)
                rightAnswerView.setBackgroundColor(Color.GREEN)
            }
        }

        runDelayed(if (selectedAnswer.isRight) ANSWER_DELAY_RIGHT else ANSWER_DELAY_WRONG) {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mainmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_resetdata -> {
                onResetData(); true
            }
            R.id.menu_showbo -> {
                onShowBo(); true
            }
            R.id.menu_showyu -> {
                onShowYu(); true
            }
            R.id.menu_about -> {
                onAbout(); true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private var shownCheatsheet = ShownCheatsheet.NONE

    private fun onShowBo() {
        LOG.i("onShowBo()")
        when (shownCheatsheet) {
            ShownCheatsheet.NONE -> {
                changeCheatsheet(R.drawable.cheatsheet_bo, ShownCheatsheet.BO)
                cheatsheetContainer.toggleVisibility()
            }
            ShownCheatsheet.BO -> {
                shownCheatsheet = ShownCheatsheet.NONE
                cheatsheetContainer.toggleVisibility()
            }
            ShownCheatsheet.YU -> {
                changeCheatsheet(R.drawable.cheatsheet_bo, ShownCheatsheet.BO)
            }
        }
    }

    private fun onShowYu() {
        LOG.i("onShowYu()")
        when (shownCheatsheet) {
            ShownCheatsheet.NONE -> {
                changeCheatsheet(R.drawable.cheatsheet_yu, ShownCheatsheet.YU)
                cheatsheetContainer.toggleVisibility()
            }
            ShownCheatsheet.YU -> {
                shownCheatsheet = ShownCheatsheet.NONE
                cheatsheetContainer.toggleVisibility()
            }
            ShownCheatsheet.BO -> {
                changeCheatsheet(R.drawable.cheatsheet_yu, ShownCheatsheet.YU)
            }
        }
    }

    private fun changeCheatsheet(drawable: Int, sheet: ShownCheatsheet) {
        cheatsheetImage.setImageResource(drawable)
        shownCheatsheet = sheet
    }

    private fun onAbout() {
        Alerts.showOkDialog(this,
                title = "Über Yobu",
                message = "Version: $GADSU_APP_VERSION\nErstellt von: Christoph")
    }

    private var shownDialog: AlertDialog? = null
    private fun onResetData() {
        LOG.i("onResetData()")
        shownDialog = AlertDialog.Builder(this)
                .setTitle("Daten löschen")
                .setMessage("Bist du dir sicher dass du alle Daten zurücksetzen willst?")
                .setCancelable(true)
                .setPositiveButton("Löschen") { dialog, which ->
                    doResetData()
//                    dialog.cancel()
                    dialog.dismiss()
                    toast("Daten wurden erfolgreich zurückgesetzt.")
                }
                .setNegativeButton("Abbrechen") { dialog, which -> dialog.dismiss() }
                .create()
        shownDialog!!.show()
    }

    private fun doResetData() {
        stats.deleteAll()
        prefs.highscore = 0
        currentHighScore = 0
        currentCountRight = 0
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

        LOG.i { "shownDialog is null: ${shownDialog == null}" }
        shownDialog?.apply {
            // TODO MainActivity has leaked window
            if (isShowing) {
                dismiss()
            }
        }
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
}

private enum class ShownCheatsheet {
    NONE, BO, YU
}
