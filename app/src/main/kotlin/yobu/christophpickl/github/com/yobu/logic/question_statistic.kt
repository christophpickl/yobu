package yobu.christophpickl.github.com.yobu.logic

import android.content.Context
import yobu.christophpickl.github.com.yobu.logic.persistence.QuestionStatisticsSqliteRepository


/**
 * Model.
 */
data class QuestionStatistic(
        val id: String,
        val countCorrect: Int
) {
    companion object // needed for (test) extensions
}


/**
 * Repository interface.
 */
interface QuestionStatisticsRepository {
    fun insertOrUpdate(statistic: QuestionStatistic)
    fun readAll(): List<QuestionStatistic>
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