package yobu.christophpickl.github.com.yobu.logic

import android.content.Context
import android.support.annotation.VisibleForTesting
import yobu.christophpickl.github.com.yobu.Question
import yobu.christophpickl.github.com.yobu.logic.persistence.QuestionStatisticsSqliteRepository
import yobu.christophpickl.github.com.yobu.misc.Clock
import yobu.christophpickl.github.com.yobu.misc.RealClock
import yobu.christophpickl.github.com.yobu.misc.associateMultiBy
import java.util.*


class QuestionStatisticService(
        private val repository: QuestionStatisticsRepository,
        private val clock: Clock = RealClock()
) {
    constructor(context: Context) : this(CachedQuestionStatisticsRepository(context, QuestionStatisticsSqliteRepository(context)))

    @VisibleForTesting fun calcPoints(statistic: QuestionStatistic): Double {
        var datePoints = 0.0
        datePoints += if (statistic.wasNotYetAnswered) 100.0 else 0.0
        datePoints += statistic.lastAnswered?.calcPoints() ?: 0.0
//        datePoints += statistic.lastRight?.calcPoints() ?: 0.0
//        datePoints += statistic.lastWrong?.calcPoints() ?: 0.0

        return statistic.countRight * -7.0 +
                statistic.countWrong * 4.0 +
                datePoints
    }

    private fun Date.calcPoints(): Double {
        val daysOld = this.diffDaysToToday()
        return daysOld * 2.0
    }

    private fun Date.diffDaysToToday(): Int {
        val diffTime = clock.now().time - this.time
        return (diffTime / (1000 * 60 * 60 * 24)).toInt()
    }

    fun rightAnswered(question: Question) {
        answered(question, isRight = true)
    }

    fun wrongAnswered(question: Question) {
        answered(question, isRight = false)
    }

    private fun answered(question: Question, isRight: Boolean) {
        val maybeStat = repository.read(question.id)

        repository.insertOrUpdate(QuestionStatistic(
                id = question.id,
                countRight = if (isRight) {
                    maybeStat?.countRight?.inc() ?: 1
                } else {
                    maybeStat?.countRight ?: 0
                },
                countWrong = if (isRight) {
                    maybeStat?.countWrong ?: 0
                } else {
                    maybeStat?.countWrong?.inc() ?: 1
                },
                lastRight = if (isRight) {
                    clock.now()
                } else {
                    maybeStat?.lastRight
                },
                lastWrong = if (isRight) {
                    maybeStat?.lastWrong
                } else {
                    clock.now()
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
        val countRight: Int,
        val countWrong: Int,
        val lastRight: Date?,
        val lastWrong: Date?
) {
    companion object // needed for (test) extensions

    val countTotal: Int get() = countRight + countWrong
    val wasYetAnswered: Boolean = lastRight == null && lastWrong == null
    val wasNotYetAnswered: Boolean = !wasYetAnswered

    // TODO test this
    val lastAnswered: Date? get() {
        if (lastRight != null && lastWrong != null) {
            return if (lastRight.time > lastWrong.time) lastRight else lastWrong
        }
        if (lastRight != null) {
            return lastRight
        }
        if (lastWrong != null) {
            return lastWrong
        }
        return null
    }
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