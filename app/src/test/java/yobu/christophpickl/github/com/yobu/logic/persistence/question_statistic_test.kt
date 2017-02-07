package yobu.christophpickl.github.com.yobu.logic.persistence

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.Test
import yobu.christophpickl.github.com.yobu.logic.QuestionStatistic
import yobu.christophpickl.github.com.yobu.testinfra.RobolectricTest

fun QuestionStatistic.Companion.testee(
        id: String = "testId",
        countCorrect: Int = 0
) = QuestionStatistic(id, countCorrect)

class QuestionStatisticsSqliteRepositoryTest : RobolectricTest() {

    @Test fun sunshine() {
        val statistic = QuestionStatistic.testee()
        withTestActivity { activity ->
            val repo = QuestionStatisticsSqliteRepository(activity)

            assertThat(repo.readAll(), emptyCollectionOf(QuestionStatistic::class.java))

            repo.insertOrUpdate(statistic)
            assertThat(repo.readAll(), contains(statistic))
        }
    }

    @Test fun insertOrUpdate_twoDifferent() {
        val statistic1 = QuestionStatistic.testee().copy(id = "id1")
        val statistic2 = QuestionStatistic.testee().copy(id = "id2")
        withTestActivity { activity ->
            val repo = QuestionStatisticsSqliteRepository(activity)

            repo.insertOrUpdate(statistic1)
            repo.insertOrUpdate(statistic2)
            assertThat(repo.readAll(), contains(statistic1, statistic2))
        }
    }

    @Test fun insertOrUpdate_twiceSameShouldUpdate() {
        val statistic = QuestionStatistic.testee().copy(countCorrect = 21)
        withTestActivity { activity ->
            val repo = QuestionStatisticsSqliteRepository(activity)

            repo.insertOrUpdate(statistic)

            val statisticChanged = statistic.copy(countCorrect = 42)
            repo.insertOrUpdate(statisticChanged)
            assertThat(repo.readAll(), allOf(hasSize(1), contains(statisticChanged)))
        }
    }

    // TODO read single test

}