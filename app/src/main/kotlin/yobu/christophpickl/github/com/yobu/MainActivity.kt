package yobu.christophpickl.github.com.yobu

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val questions = QuestionRepo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtOutput = findViewById(R.id.txtOutput) as TextView
        val btnNextQuestion = findViewById(R.id.btnNextQuestion) as Button
        btnNextQuestion.setOnClickListener {
            println("btnNextQuestion clicked")
            val question = questions.nextQuestion()
            txtOutput.text = question.text
        }
    }


}
