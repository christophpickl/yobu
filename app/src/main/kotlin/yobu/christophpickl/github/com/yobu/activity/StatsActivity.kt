package yobu.christophpickl.github.com.yobu.activity

import android.R
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.ColorInt
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.View.TEXT_ALIGNMENT_TEXT_END
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewManager
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.RelativeLayout
import org.jetbrains.anko.*
import yobu.christophpickl.github.com.yobu.Colors
import yobu.christophpickl.github.com.yobu.Question
import yobu.christophpickl.github.com.yobu.common.LOG
import yobu.christophpickl.github.com.yobu.common.textViewX
import yobu.christophpickl.github.com.yobu.debugColor
import yobu.christophpickl.github.com.yobu.logic.GlobalDb
import yobu.christophpickl.github.com.yobu.logic.GlobalQuestions
import yobu.christophpickl.github.com.yobu.logic.QuestionStatistic


class StatsActivity : AppCompatActivity() {

    private val repo by lazy { GlobalDb.getRepo(this) }

    // QuestionStatisticsRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatsActivityUi(generateStatsText()).setContentView(this)
    }

    fun onFinish() {
        finish()
    }

    private fun generateStatsText(): List<QuestionStatistic> {
        val answeredStats = repo.readAll()
        val allQuestions = GlobalQuestions.allQuestions
        val unanswerdStats = allQuestions
                .map { it.id }
                .minus(answeredStats.map { it.id })
                .map { id -> QuestionStatistic(id, 0, 0, null, null) }
        val allStats = answeredStats.plus(unanswerdStats).sortedBy { it.id }

        return allStats.sortedBy { it.id }
//        return "ID - OK / KO\n============\n" +
//                allStats.sortedBy { it.id }
//                        .map { "${it.id} - ${it.countRight}/${it.countWrong}" }
//                        .joinToString("\n")
    }
}

// example: https://github.com/yanex/anko-example/blob/master/app/src/main/java/org/example/ankodemo/MainActivity.kt
class StatsActivityUi(private val stats: List<QuestionStatistic>) : AnkoComponent<StatsActivity> {

    companion object {
        private val LOG = LOG(StatsActivityUi::class.java)
    }

    @SuppressLint("SetTextI18n")
    override fun createView(ui: AnkoContext<StatsActivity>) = with(ui) {
        verticalLayout {
            padding = ui.dip(20)

            listView {
                val questionAdapter = QuestionStatisticAdapter(stats)
                adapter = questionAdapter

                onItemClick { adapterView, view, position, id ->
                    val questionId = questionAdapter.getItem(position).id
                    LOG.i("IMPLEMENT ME: on question stat clicked: $questionId")
                    // FIXME start MainActivity via intent and pass questionId
                }
            }

//            button("Zurück") {
//                // MINOR could display the back button in top panel where the context menu is in main activity
//                onClick { ui.owner.onFinish() }
//            }
        }
    }

}

class QuestionStatisticAdapter(private val stats: List<QuestionStatistic>) : BaseAdapter() {


    override fun getView(i: Int, v: View?, parent: ViewGroup?): View {
        val item = getItem(i)

        return with(parent!!.context) {
            relativeLayout {
                textView {
                    text = item.id
                    lparams {
                        alignParentLeft()
                    }
                }
                linearLayout {
                    layoutParams = RelativeLayout.LayoutParams(wrapContent, wrapContent).apply {
                        alignParentRight()
                    }
                    horizontalPadding = dip(0)
                    orientation = HORIZONTAL
                    // or textView("<font color=foo>1</font>...")
                    textViewX(item.countRight.toString(), Colors.QuestionRight)
                    textViewX(" / ")
                    textViewX(item.countWrong.toString(), Colors.QuestionWrong)
                }
            }
        }
    }



    override fun getItem(position: Int) = stats.get(position)
    override fun getCount() = stats.size
    override fun getItemId(position: Int) = getItem(position).id.hashCode().toLong()

}