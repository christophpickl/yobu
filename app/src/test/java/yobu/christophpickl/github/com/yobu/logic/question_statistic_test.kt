package yobu.christophpickl.github.com.yobu.logic

import com.nhaarman.mockito_kotlin.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.emptyCollectionOf
import org.junit.Test
import org.mockito.InOrder
import org.mockito.Mockito
import yobu.christophpickl.github.com.yobu.Question
import yobu.christophpickl.github.com.yobu.logic.persistence.testInstance
import yobu.christophpickl.github.com.yobu.testee
import yobu.christophpickl.github.com.yobu.testinfra.RobolectricTest

class CachedQuestionStatisticsRepositoryTest : RobolectricTest() {

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
// TODO class QuestionStatisticServiceIT ... wire all objects manually (real sqlite)

class QuestionStatisticServiceTest : RobolectricTest() {

    private val question = Question.testee()
    private val mockRepo = mock<QuestionStatisticsRepository>()
    private val service = QuestionStatisticService(mockRepo)

    @Test fun correctAnswered_insertsNewQuestionStatistic() {
        withTestActivity { activity ->
            service.correctAnswered(question)
        }
        verify(mockRepo).insertOrUpdate(QuestionStatistic(question.id, 1))
    }

    @Test fun correctAnswered_twice_insertsNewQuestionStatisticWithCount2() {
        whenever(mockRepo.read(question.id))
                .thenReturn(null)
                .thenReturn(QuestionStatistic(question.id, 1))

        withTestActivity { activity ->
            service.correctAnswered(question)
            service.correctAnswered(question)
        }

        val inOrder = inOrder(mockRepo)
        inOrder.verify(mockRepo).insertOrUpdate(QuestionStatistic(question.id, 1))
        inOrder.verify(mockRepo).insertOrUpdate(QuestionStatistic(question.id, 2))
    }

    @Test fun wrongAnswered_insertsNewQuestionStatistic() {
        withTestActivity { activity ->
            service.wrongAnswered(question)
        }
        verify(mockRepo).insertOrUpdate(QuestionStatistic(question.id, 0)) // TODO finish
    }

    // TODO write more tests
}
