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
import yobu.christophpickl.github.com.yobu.testee
import yobu.christophpickl.github.com.yobu.testinfra.RobolectricTest


private val question1 = Question.testee(id = "q1")
private val question2 = Question.testee(id = "q2")
private val answered = Question.testee(id = "answered")
private val answered1 = Question.testee(id = "answered1")
private val answered2 = Question.testee(id = "answered2")
private val unanswered = Question.testee(id = "unanswered")

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
// TODO class QuestionStatisticServiceIT ... wire all objects manually (real sqlite)

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
}



private fun Question.toStatistic(countCorrect: Int = 1) =
        QuestionStatistic(this.id, countCorrect)
