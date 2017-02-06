package yobu.christophpickl.github.com.yobu.logic

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.emptyCollectionOf
import org.junit.Test
import org.mockito.Mockito
import yobu.christophpickl.github.com.yobu.logic.persistence.testInstance
import yobu.christophpickl.github.com.yobu.testinfra.RobolectricTest

class CachedQuestionStatisticsRepositoryTest  : RobolectricTest() {

    @Test fun readAll_onlyInvokedOnce() {
        val statistic = QuestionStatistic.testInstance()
        val mockSqlite = mock<QuestionStatisticsRepository> {
            on { readAll() } doReturn emptyList<QuestionStatistic>()
        }

        withTestActivity { activity ->
            val repo = CachedQuestionStatisticsRepository(activity, mockSqlite)

            assertThat(repo.readAll(), emptyCollectionOf(QuestionStatistic::class.java))

            repo.insertOrUpdate(statistic)
            assertThat(repo.readAll(), contains(statistic))
            repo.readAll()
            repo.readAll()
        }

        verify(mockSqlite, times(1)).readAll()
    }
}