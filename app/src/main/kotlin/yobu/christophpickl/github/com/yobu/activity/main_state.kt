package yobu.christophpickl.github.com.yobu.activity

import android.os.Bundle
import yobu.christophpickl.github.com.yobu.Answer
import yobu.christophpickl.github.com.yobu.Question


data class YobuState(
        val countRight: Int,
        val question: Question
)

object StateManager {

    private val LOG = yobu.christophpickl.github.com.yobu.common.LOG(StateManager::class.java)
    private val ANSWER_FIELD_SEPARATOR = "\t"

    private val KEY_COUNT_RIGHT = "countRight"
    private val KEY_QUESTION_ID = "question.id"
    private val KEY_QUESTION_TEXT = "question.text"
    private val KEY_QUESTION_TITLE = "question.title"
    private val KEY_ANSWERS = "answers"

    fun read(bundle: Bundle): YobuState {
        LOG.i("read(bundle)")
        return YobuState(
                countRight = bundle.getInt(KEY_COUNT_RIGHT),
                question = bundle.readQuestion()
        )
    }


    fun save(bundle: Bundle, state: YobuState) {
        LOG.i { "save(bundle, state=$state)" }
        bundle.putInt(KEY_COUNT_RIGHT, state.countRight)
        bundle.putQuestion(state.question)
    }
//        putStringArray(KEY_ANSWERS, )

    private fun Bundle.readQuestion(): Question {
        return Question(
                id = getString(KEY_QUESTION_ID),
                text = getString(KEY_QUESTION_TEXT),
                title = getString(KEY_QUESTION_TITLE),
                answers = readAnswers()
        )
    }

    private fun Bundle.readAnswers(): List<Answer> {
        return getStringArray(KEY_ANSWERS).map {
            val fields = it.split(ANSWER_FIELD_SEPARATOR)
            if (fields.size != 2) throw IllegalStateException("Invalid question answer in bundle state: '$it'!")
            Answer(fields[0], fields[1].toBoolean())
        }
    }

    private fun Bundle.putQuestion(question: Question) {
        putString(KEY_QUESTION_ID, question.id)
        putString(KEY_QUESTION_TEXT, question.text)
        putString(KEY_QUESTION_TITLE, question.title)

        putStringArray(KEY_ANSWERS, question.answers.map {
            listOf(it.text, it.isRight.toString()).joinToString(ANSWER_FIELD_SEPARATOR)
        }.toTypedArray())
    }

}
