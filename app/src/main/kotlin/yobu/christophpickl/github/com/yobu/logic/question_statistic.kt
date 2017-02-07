package yobu.christophpickl.github.com.yobu.logic

import android.content.Context
import android.support.annotation.VisibleForTesting
import yobu.christophpickl.github.com.yobu.Question
import yobu.christophpickl.github.com.yobu.logic.persistence.QuestionStatisticsSqliteRepository
import yobu.christophpickl.github.com.yobu.misc.associateMultiBy
import java.util.*

class QuestionStatisticService(
        private val repository: QuestionStatisticsRepository
) {
    constructor(context: Context) : this(CachedQuestionStatisticsRepository(context, QuestionStatisticsSqliteRepository(context)))

    companion object {
        @VisibleForTesting fun calcPoints(statistic: QuestionStatistic): Double {
            return statistic.countCorrect * -10.0
        }
    }

    fun correctAnswered(question: Question) {
        answered(question, correct = true)
    }

    fun wrongAnswered(question: Question) {
        answered(question, correct = false)
    }

    private fun answered(question: Question, correct: Boolean) {
        val maybeStat = repository.read(question.id)

        repository.insertOrUpdate(QuestionStatistic(
                id = question.id,
                countCorrect = if (correct) {
                    maybeStat?.countCorrect?.inc() ?: 1
                } else {
                    maybeStat?.countCorrect ?: 0
                }
        ))
    }

    fun nextQuestion(questionsById: Map<String, Question>): Question {
        if (questionsById.isEmpty()) throw IllegalArgumentException("empty questions map is not allowed!")
        val answeredStats: List<QuestionStatistic> = repository.readAll()

        val unansweredQuestionIds = questionsById.keys.minus(answeredStats.map { it.id })
        if (unansweredQuestionIds.isNotEmpty()) {
            val nextQuestionId = unansweredQuestionIds.randomElement()
            return questionsById.getOrThrow(nextQuestionId)
        }

        // all questions have been answered at least once
        val statsByPoints = answeredStats.associateMultiBy { calcPoints(it) }
        val highestPointsStats = statsByPoints[statsByPoints.keys.max()]!!
        return questionsById.getOrThrow(highestPointsStats.randomElement().id)
    }

    private fun Map<String, Question>.getOrThrow(id: String) =
            this[id] ?: throw RuntimeException("Could not question by ID: $id in questions map: $values")

}

/**
 * Model.
 */
data class QuestionStatistic(
        val id: String,
        val countCorrect: Int
//        val countWrong: Int,
//        val lastCorrect: Date?,
//        val lastWrong: Date?
) {
    companion object // needed for (test) extensions

//    val countTotal: Int get() = countCorrect + countWrong
//    val lastAnswered: Date? get()  {
//        return null
//    }
}


/**
 * Repository interface.
 */
interface QuestionStatisticsRepository {
    fun insertOrUpdate(statistic: QuestionStatistic)
    fun readAll(): List<QuestionStatistic>
    fun read(id: String): QuestionStatistic?
}

/**
 * Avoid readAll communication to sqlite by caching.
 */
class CachedQuestionStatisticsRepository(
        context: Context,
        private val sqliteDelegate: QuestionStatisticsRepository = QuestionStatisticsSqliteRepository(context)
) : QuestionStatisticsRepository {

    private val cache = mutableMapOf<String, QuestionStatistic>()
    private var firstReadAll = true

    override fun read(id: String): QuestionStatistic? = cache[id]

    override fun readAll(): List<QuestionStatistic> {
        if (firstReadAll) {
            cache.putAll(sqliteDelegate.readAll().associateBy { it.id })
            firstReadAll = false
        }
        return cache.values.toList()
    }

    override fun insertOrUpdate(statistic: QuestionStatistic) {
        cache.put(statistic.id, statistic)
        sqliteDelegate.insertOrUpdate(statistic)
    }

}