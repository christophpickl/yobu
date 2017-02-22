package yobu.christophpickl.github.com.yobu.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import org.jetbrains.anko.*
import yobu.christophpickl.github.com.yobu.MyColor
import yobu.christophpickl.github.com.yobu.common.LOG
import yobu.christophpickl.github.com.yobu.common.htmlText
import yobu.christophpickl.github.com.yobu.logic.GlobalDb
import yobu.christophpickl.github.com.yobu.logic.GlobalQuestions
import yobu.christophpickl.github.com.yobu.logic.QuestionStatistic
import yobu.christophpickl.github.com.yobu.logic.QuestionStatisticsRepository

class StatsActivity : AppCompatActivity() {


    private val repo: QuestionStatisticsRepository by lazy { GlobalDb.getStatisticsRepo(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatsActivityUi(generateStatsText()).setContentView(this)
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
                scrollBarStyle = View.SCROLLBARS_OUTSIDE_INSET

                val questionAdapter = QuestionStatisticAdapter(stats)
                adapter = questionAdapter

                onItemClick { adapterView, view, position, id ->
                    val questionId = questionAdapter.getItem(position).id
                    LOG.i { "Item clicked for question with ID: $questionId" }
                    startActivity<MainActivity>(
                            MainActivity.INTENT_QUESTION_ID to questionId
                    )
                }
            }
        }
    }
}


class QuestionStatisticAdapter(private val stats: List<QuestionStatistic>) : BaseAdapter() {

    override fun getView(i: Int, v: View?, parent: ViewGroup?): View {
        val stat = getItem(i)

        return with(parent!!.context) {
            relativeLayout {
                textView {
                    text = stat.id
                    lparams {
                        alignParentLeft()
                    }
                }
                textView {
                    htmlText = """<font color="${MyColor.QuestionRight.hexStringVal}">${stat.countRight}</font> / <font color="${MyColor.QuestionWrong.hexStringVal}">${stat.countWrong}</font>"""
                    lparams {
                        alignParentRight()
                    }
                }

            }
        }
    }

    override fun getItem(position: Int) = stats[position]
    override fun getCount() = stats.size
    override fun getItemId(position: Int) = getItem(position).id.hashCode().toLong()

}
