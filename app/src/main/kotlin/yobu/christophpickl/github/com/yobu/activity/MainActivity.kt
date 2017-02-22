package yobu.christophpickl.github.com.yobu.activity

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.KodeinAppCompatActivity
import com.github.salomonbrys.kodein.instance
import com.pawegio.kandroid.find
import com.pawegio.kandroid.runDelayed
import com.pawegio.kandroid.startActivity
import com.pawegio.kandroid.toast
import yobu.christophpickl.github.com.yobu.*
import yobu.christophpickl.github.com.yobu.activity.view.AnswersListAdapter
import yobu.christophpickl.github.com.yobu.common.*
import yobu.christophpickl.github.com.yobu.logic.*
import yobu.christophpickl.github.com.yobu.logic.persistence.Preferences
import yobu.christophpickl.github.com.yobu.logic.persistence.createPreferences

class MainActivity : KodeinAppCompatActivity() {

    companion object {
        val INTENT_QUESTION_ID = "questionId"

        private val log = LOG(MainActivity::class.java)
        private val ANSWER_DELAY_RIGHT = 200L
        private val ANSWER_DELAY_WRONG = 500L
    }

    private val prefs: Preferences by instance()
    private val stats: StatisticService by instance()
    private val loader: QuestionLoader by instance()

    private val txtOutput by lazy { find<TextView>(R.id.txtOutput) }
    private val txtScoreAndHighscore by lazy { find<TextView>(R.id.txtScoreAndHighscore) }
    private val cheatsheetImage by lazy { find<ImageView>(R.id.cheatsheet_image) }
    private val cheatsheetContainer by lazy {
        find<RelativeLayout>(R.id.cheatsheet_container).apply {
            onClickMakeGone() {
                shownCheatsheet = ShownCheatsheet.NONE
            }
        }
    }
    private val answersList by lazy {
        find<ListView>(R.id.answersList).apply {
            setOnItemClickListener { parent, view, position, id ->
                log.d("answersList.onItemClickListener on position: $position")
                val answerLabel = view.find<TextView>(R.id.answerLabel)
                onAnswerClicked(currentQuestion!!, currentQuestion!!.answers[position], answerLabel)
            }
        }
    }

    private var shownDialog: AlertDialog? = null
    private var shownCheatsheet = ShownCheatsheet.NONE

    private var currentQuestion: Question? = null
    private var currentHighScore = 0
        get() = field
        set(value) {
            field = value
            updateTxtCountRight()
        }
    private var currentScore: Int = 0
        get() = field
        set(value) {
            field = value
            updateTxtCountRight()
        }


    override fun onCreate(savedInstanceState: Bundle?) { // vs: onRestoreInstanceState
        log.i("onCreate(savedInstanceState.isNull=${savedInstanceState == null})")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayYobuLogo()

        currentHighScore = prefs.highscore

        if (savedInstanceState != null) {
            val yobuState = StateManager.read(savedInstanceState)
            log.d { "Restoring state from: $yobuState" }
            currentScore = yobuState.countRight
            changeQuestion(yobuState.question)
        } else {
            val intentQuestionId: String? = intent.getStringExtra(INTENT_QUESTION_ID)
            log.i("No instance state saved.")

            if (intentQuestionId != null) {
                changeQuestion(loader.questionById(intentQuestionId))
                currentScore = prefs.currentScore
                log.i { "Question ID from intent: $intentQuestionId; currentScore: $currentScore " }
            } else {
                updateTxtCountRight() // force highscore text value update
                onNextQuestion()
            }
        }
    }

    override fun onSaveInstanceState(state: Bundle?) {
        log.i { "onSaveInstanceState(state.isNull=${state == null})" }
        super.onSaveInstanceState(state)
        if (state == null) {
            log.w("Not saving state as of null bundle!")
            return
        }

        // maybe we are navigating away (statistics) and return back here again
        prefs.currentScore = currentScore

        StateManager.save(state, YobuState(
                countRight = currentScore,
                question = currentQuestion!!
        ))
    }

    override fun onRestoreInstanceState(state: Bundle?) {
        log.i { "onRestoreInstanceState(state.isNull=${state == null})" }
        super.onRestoreInstanceState(state)
    }

    private fun onNextQuestion() {
        log.d("onNextQuestion()")

        val question = stats.nextQuestion()
        changeQuestion(question.copy(answers = question.answers.randomizeElements())) // OR: sortedBy { it.text }
    }

    private fun updateTxtCountRight() {
        txtScoreAndHighscore.text = "$currentScore / $currentHighScore"
    }

    private fun changeQuestion(newQuestion: Question) {
        currentQuestion = newQuestion

        txtOutput.text = currentQuestion!!.text
        answersList.adapter = AnswersListAdapter(this, currentQuestion!!.answers)
    }

    private fun onAnswerClicked(question: Question, selectedAnswer: Answer, answerLabel: TextView) {
        answersList.isEnabled = false
        answerLabel.setBackgroundColor(if (selectedAnswer.isRight) Colors.QuestionRight else Colors.QuestionWrong)

        if (selectedAnswer.isRight) {
            stats.rightAnswered(question)
            currentScore++
            if (currentScore - 1 == currentHighScore) {
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
        val newHighscore = currentScore
        if (newHighscore > oldHighscore) {
            toast("Juchu, neue Highscore: $newHighscore!")
            prefs.highscore = newHighscore
            currentHighScore = newHighscore
        }

        currentScore = 0
        onNextQuestion()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mainmenu, menu)
        return true
    }

    private val menuActions = mapOf(
            R.id.menu_resetdata to { onResetData() },
            R.id.menu_showbo to { onShowBo() },
            R.id.menu_showyu to { onShowYu() },
            R.id.menu_about to { onAbout() },
            R.id.menu_stats to { startActivity<StatsActivity>() }
    )

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        menuActions[item.itemId]?.invoke() ?: return super.onOptionsItemSelected(item)
        return true
    }

    private fun onShowBo() {
        log.i("onShowBo()")
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
        log.i("onShowYu()")
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
        AlertDialog.Builder(this)
                .setTitle("Über Yobu")
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Schließen", null)
                .setHtmlView(this, "Version: $GADSU_APP_VERSION\nhttps://github.com/christophpickl/yobu")
                .create()
                .show()
    }


    private fun onResetData() {
        log.i("onResetData()")
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
        currentScore = 0
    }

    override fun onStart() {
        log.i("onStart()")
        super.onStart()
    }

    override fun onResume() {
        log.i("onResume()")
        super.onResume()
    }

    override fun onPause() { // vs: onSaveInstanceState
        log.i("onPause()")
        super.onPause()

        log.i { "shownDialog is null: ${shownDialog == null}" }
        shownDialog?.apply {
            if (isShowing) {
                dismiss()
            }
        }
    }

    override fun onStop() {
        log.i("onStop()")
        super.onStop()
    }

    override fun onRestart() {
        log.i("onRestart()")
        super.onRestart()
    }

    override fun onDestroy() {
        log.i("onDestroy()")
        super.onDestroy()
    }
}

private enum class ShownCheatsheet {
    NONE, BO, YU
}
