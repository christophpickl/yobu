package yobu.christophpickl.github.com.yobu.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout.HORIZONTAL
import android.widget.RelativeLayout
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.KodeinAppCompatActivity
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import org.jetbrains.anko.*
import yobu.christophpickl.github.com.yobu.Colors
import yobu.christophpickl.github.com.yobu.common.LOG
import yobu.christophpickl.github.com.yobu.common.textViewX
import yobu.christophpickl.github.com.yobu.logic.QuestionLoader
import yobu.christophpickl.github.com.yobu.logic.QuestionStatistic
import yobu.christophpickl.github.com.yobu.logic.QuestionStatisticsRepository


class StatsActivity : KodeinAppCompatActivity() {

    private val repo: QuestionStatisticsRepository by instance()
    private val loader: QuestionLoader by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        displayYobuLogo()

        StatsActivityUi(generateStatsText()).setContentView(this)
    }

    private fun generateStatsText(): List<QuestionStatistic> {
        val answeredStats = repo.readAll()
        val allQuestions = loader.questions
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