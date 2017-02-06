package yobu.christophpickl.github.com.yobu.logic.persistence

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.emptyCollectionOf
import org.junit.Test
import yobu.christophpickl.github.com.yobu.testinfra.RobolectricTest

fun QuestionStatistic.Companion.testInstance() = QuestionStatistic(
        id = "testId"
)

class QuestionStatisticsSqliteRepositoryTest : RobolectricTest() {

    @Test
    fun foo() {
        val statistic = QuestionStatistic.testInstance()

        withTestActivity { activity ->
            val repo = QuestionStatisticsSqliteRepository(activity)

            assertThat(repo.readAll(),
                    emptyCollectionOf(QuestionStatistic::class.java))

            repo.insertOrUpdate(statistic)
            assertThat(repo.readAll(),
                    contains(statistic))
        }


    }

}