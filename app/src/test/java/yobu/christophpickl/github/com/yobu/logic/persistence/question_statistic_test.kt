package yobu.christophpickl.github.com.yobu.logic.persistence

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.Test
import yobu.christophpickl.github.com.yobu.logic.QuestionStatistic
import yobu.christophpickl.github.com.yobu.testinfra.RobolectricTest
import java.util.*

fun QuestionStatistic.Companion.testee(
        id: String = "testId",
        countRight: Int = 0,
        countWrong: Int = 0,
        lastRight: Date? = null,
        lastWrong: Date? = null
) = QuestionStatistic(id, countRight, countWrong, lastRight, lastWrong)

class QuestionStatisticsSqliteRepositoryTest : RobolectricTest() {

    private val statistic = QuestionStatistic.testee()
    private val statistic1 = statistic.copy(id = "id1")
    private val statistic2 = statistic.copy(id = "id2")

    @Test fun sunshine() {

        withTestActivity { activity ->
            val repo = QuestionStatisticsSqliteRepository(activity)

            assertThat(repo.readAll(), emptyCollectionOf(QuestionStatistic::class.java))

            repo.insertOrUpdate(statistic)
            assertThat(repo.readAll(), contains(statistic))
        }
    }

    @Test fun insertOrUpdate_twoDifferent() {
        withTestActivity { activity ->
            val repo = QuestionStatisticsSqliteRepository(activity)

            repo.insertOrUpdate(statistic1)
            repo.insertOrUpdate(statistic2)
            assertThat(repo.readAll(), contains(statistic1, statistic2))
        }
    }

    @Test fun insertOrUpdate_twiceSameShouldUpdate() {
        val rightStat = statistic.copy(countRight = 21)
        withTestActivity { activity ->
            val repo = QuestionStatisticsSqliteRepository(activity)

            repo.insertOrUpdate(rightStat)

            val statisticChanged = rightStat.copy(countRight = 42)
            repo.insertOrUpdate(statisticChanged)
            assertThat(repo.readAll(), allOf(hasSize(1), contains(statisticChanged)))
        }
    }

    @Test fun readSingle() {
        withTestActivity { activity ->
            val repo = QuestionStatisticsSqliteRepository(activity)
            val testee = QuestionStatistic.testee()

            assertThat(repo.read(testee.id), nullValue())

            repo.insertOrUpdate(testee)
            assertThat(repo.read(testee.id), equalTo(testee))
        }
    }

}