package yobu.christophpickl.github.com.yobu

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.pawegio.kandroid.find

class MainActivity : AppCompatActivity() {

    companion object {
        private val LOG = LOG(javaClass)
    }

    private val questions = QuestionRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG.i("onCreate(..)")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtOutput = find<TextView>(R.id.txtOutput)
        val btnNextQuestion = find<Button>(R.id.btnNextQuestion)
        val answersList = find<ListView>(R.id.answersList)

        btnNextQuestion.setOnClickListener {
            btnNextQuestion.text = "Next question"
            LOG.d("btnNextQuestion clicked")

            val question = questions.nextQuestion()
            txtOutput.text = question.text

            answersList.adapter = ArrayAdapter<String>(this, R.layout.list_answer, question.answers.map { it.text })
            answersList.setOnItemClickListener { parent, view, position, id ->
                LOG.d("onItemClickListener on position: $position, id: $id, view: $view")
            }
        }
    }

}
