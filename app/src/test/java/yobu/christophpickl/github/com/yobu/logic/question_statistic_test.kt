package yobu.christophpickl.github.com.yobu.logic

import com.nhaarman.mockito_kotlin.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.Test
import org.mockito.InOrder
import org.mockito.Mockito
import yobu.christophpickl.github.com.yobu.Question
import yobu.christophpickl.github.com.yobu.logic.persistence.testee
import yobu.christophpickl.github.com.yobu.misc.Clock
import yobu.christophpickl.github.com.yobu.misc.parseDateTime
import yobu.christophpickl.github.com.yobu.testee
import yobu.christophpickl.github.com.yobu.testinfra.RobolectricTest


private val question1 = Question.testee(id = "q1")
private val question2 = Question.testee(id = "q2")
private val answered = Question.testee(id = "answered")
private val answered1 = Question.testee(id = "answered1")
private val answered2 = Question.testee(id = "answered2")
private val unanswered = Question.testee(id = "unanswered")


val TEST_DATE_STRING = "2017-12-31 12:59:42"
val TEST_DATE = TEST_DATE_STRING.parseDateTime()

val TEST_DATE1_STRING = "2001-01-11 11:11:11"
val TEST_DATE1 = TEST_DATE_STRING.parseDateTime()
val TEST_DATE2_STRING = "2002-02-22 22:22:22"
val TEST_DATE2 = TEST_DATE_STRING.parseDateTime()

class CachedQuestionStatisticsRepositoryTest : RobolectricTest() {

    @Test fun readAll_onlyInvokedOnce() {
        val statistic = QuestionStatistic.testee()
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

class QuestionRepoIT : RobolectricTest() {
    @Test fun answerFirstQuestion_nextQuestionShouldBeSecondQuestion() {
        withTestActivity { activity ->
            val questions = listOf(question1, question2)
            val stats = QuestionStatisticService(activity)
            val repo = QuestionRepo(questions, stats)

            val firstQuestion = repo.nextQuestion()

            stats.correctAnswered(firstQuestion)

            val secondQuestion = repo.nextQuestion()
            assertThat(secondQuestion, not(equalTo(firstQuestion)))
        }
    }
}


class QuestionStatisticServiceTest : RobolectricTest() {



    private val question = Question.testee()
    private val mockRepo = mock<QuestionStatisticsRepository>()
    private val mockClock = mock<Clock>()
    private val service = QuestionStatisticService(mockRepo, mockClock)

    @Test fun correctAnswered_insertsNewQuestionStatistic() {
        setDefaultClock()
        withTestActivity { activity ->
            service.correctAnswered(question)
        }

        verify(mockRepo).insertOrUpdate(QuestionStatistic.testee(id = question.id, countCorrect = 1, lastCorrect = TEST_DATE))
    }

    @Test fun correctAnswered_twice_insertsNewQuestionStatisticWithCount2() {
        setDefaultClock()
        whenever(mockRepo.read(question.id))
                .thenReturn(null)
                .thenReturn(QuestionStatistic.testee(id = question.id, countCorrect = 1)) // TODO set lastCorrect

        withTestActivity { activity ->
            service.correctAnswered(question)
            service.correctAnswered(question)
        }

        val inOrder = inOrder(mockRepo)
        inOrder.verify(mockRepo).insertOrUpdate(QuestionStatistic.testee(id = question.id, countCorrect = 1, lastCorrect = TEST_DATE))
        inOrder.verify(mockRepo).insertOrUpdate(QuestionStatistic.testee(id = question.id, countCorrect = 2, lastCorrect = TEST_DATE))
    }

    @Test fun answerCorrectAndWrong() {
        val statisticAfterCorrect = QuestionStatistic.testee(id = question.id, countCorrect = 1, lastCorrect = TEST_DATE1)

        whenever(mockClock.now())
                .thenReturn(TEST_DATE1)
                .thenReturn(TEST_DATE2)
        whenever(mockRepo.read(question.id))
                .thenReturn(null)
                .thenReturn(statisticAfterCorrect)

        withTestActivity { activity ->
            service.correctAnswered(question)
            service.wrongAnswered(question)
        }

        inOrder(mockRepo).apply {
            verify(mockRepo).insertOrUpdate(statisticAfterCorrect)
            verify(mockRepo).insertOrUpdate(statisticAfterCorrect.copy(countWrong = 1, lastWrong = TEST_DATE2))
        }
    }

    @Test fun wrongAnswered_insertsNewQuestionStatistic() {
        setDefaultClock()
        withTestActivity { activity ->
            service.wrongAnswered(question)
        }
        verify(mockRepo).insertOrUpdate(QuestionStatistic.testee(id = question.id, countWrong = 1, lastWrong = TEST_DATE))
    }

    @Test(expected = IllegalArgumentException::class)
    fun nextQuestion_emptyThrows() {
        service.nextQuestion(emptyMap())
    }

    @Test fun nextQuestion_givenOneCorrectAnsweredAndOneUnanswered_returnsUnanswered() {
        whenMockRepoReadAllReturn(answered.toStatistic())

        assertThat(nextQuestion(answered, unanswered),
                equalTo(unanswered))
    }

    @Test fun nextQuestion_givenTwoCorrectAnsweredWithDifferentCountCorrect_returnsOneWithLessCountCorrect() {
        whenMockRepoReadAllReturn(
                answered1.toStatistic(countCorrect = 1),
                answered2.toStatistic(countCorrect = 2))

        assertThat(nextQuestion(answered1, answered2),
                equalTo(answered1))
    }

    private val statCorrect0 = QuestionStatistic.testee(countCorrect = 0)
    private val statCorrect1 = QuestionStatistic.testee(countCorrect = 1)

    @Test fun calcPoints_countCorrect() {
        assertBiggerPoints(statCorrect0, statCorrect1)
    }

    private fun assertBiggerPoints(bigger: QuestionStatistic, lower: QuestionStatistic) {
        assertThat(QuestionStatisticService.calcPoints(bigger),
                greaterThan(QuestionStatisticService.calcPoints(lower)))
    }

    private fun nextQuestion(vararg questions: Question) =
        service.nextQuestion(listOf(*questions).associateBy { it.id})

    private fun whenMockRepoReadAllReturn(vararg questionStatistic: QuestionStatistic) {
        whenever(mockRepo.readAll())
                .thenReturn(listOf(*questionStatistic))
    }

    private fun setDefaultClock(date: String = TEST_DATE_STRING) {
        whenever(mockClock.now()).thenReturn(date.parseDateTime())
    }

}



private fun Question.toStatistic(countCorrect: Int = 1) =
        QuestionStatistic(this.id, countCorrect, 0, null, null)
