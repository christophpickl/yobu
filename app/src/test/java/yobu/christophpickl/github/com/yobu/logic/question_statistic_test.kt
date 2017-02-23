package yobu.christophpickl.github.com.yobu.logic

import yobu.christophpickl.github.com.yobu.Question
import yobu.christophpickl.github.com.yobu.common.Clock
import yobu.christophpickl.github.com.yobu.common.RealClock
import yobu.christophpickl.github.com.yobu.common.YobuException
import yobu.christophpickl.github.com.yobu.common.parseDateTime
import yobu.christophpickl.github.com.yobu.logic.persistence.QuestionStatisticsSqliteRepository
import yobu.christophpickl.github.com.yobu.logic.persistence.testee
import yobu.christophpickl.github.com.yobu.testee
import yobu.christophpickl.github.com.yobu.testinfra.RobolectricTest
import java.util.*


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
            val repo = CachedQuestionStatisticsRepository(mockSqlite)

            assertThat(repo.readAll(), emptyCollectionOf(QuestionStatistic::class.java))

            repo.insertOrUpdate(statistic)
            assertThat(repo.readAll(), contains(statistic))
            repo.readAll()
            repo.readAll()
        }

        verify(mockSqlite, times(1)).readAll()
    }
}
class StubbedQuestionLoader(
        override val questions: List<Question>
) : QuestionLoader {
    override val questionsById by lazy { questions.associateBy { it.id } }
    override fun questionById(id: String) = questionsById[id] ?: throw YobuException("Question not found by ID: '$id'!")
}


class QuestionRepoIT : RobolectricTest() {
    @Test fun answerFirstQuestion_nextQuestionShouldBeSecondQuestion() {
        withTestActivity { activity ->
            val questions = listOf(question1, question2)
            val repo = QuestionStatisticsSqliteRepository(activity)
            val stats = StatisticServiceImpl(repo, StubbedQuestionLoader(questions), RealClock)

            val firstQuestion = stats.nextQuestion()

            stats.rightAnswered(firstQuestion)

            val secondQuestion = stats.nextQuestion()
            assertThat(secondQuestion, not(equalTo(firstQuestion)))
        }
    }
}


class StatisticServiceTest : RobolectricTest() {

    companion object {
        private val NOW_STRING = "2017-01-01 00:21:42"
        private val NOW = NOW_STRING.parseDateTime()
    }

    private val question = Question.testee()
    private val mockRepo = mock<QuestionStatisticsRepository>()
    private val mockLoader = mock<QuestionLoader>()
    private val mockClock = mock<Clock>()
    private val service = StatisticServiceImpl(mockRepo, mockLoader, mockClock)

    @Test fun rightAnswered_insertsNewQuestionStatistic() {
        setDefaultClock()
        withTestActivity { activity ->
            service.rightAnswered(question)
        }

        verify(mockRepo).insertOrUpdate(QuestionStatistic.testee(id = question.id, countRight = 1, lastRight = NOW))
    }

    @Test fun rightAnswered_twice_insertsNewQuestionStatisticWithCount2() {
        setDefaultClock()
        whenever(mockRepo.read(question.id))
                .thenReturn(null)
                .thenReturn(QuestionStatistic.testee(id = question.id, countRight = 1)) // MINOR TEST set lastRight

        withTestActivity { activity ->
            service.rightAnswered(question)
            service.rightAnswered(question)
        }

        val inOrder = inOrder(mockRepo)
        inOrder.verify(mockRepo).insertOrUpdate(QuestionStatistic.testee(id = question.id, countRight = 1, lastRight = NOW))
        inOrder.verify(mockRepo).insertOrUpdate(QuestionStatistic.testee(id = question.id, countRight = 2, lastRight = NOW))
    }

    @Test fun answerRightAndWrong() {
        val statisticAfterRight = QuestionStatistic.testee(id = question.id, countRight = 1, lastRight = TEST_DATE1)

        whenever(mockClock.now())
                .thenReturn(TEST_DATE1)
                .thenReturn(TEST_DATE2)
        whenever(mockRepo.read(question.id))
                .thenReturn(null)
                .thenReturn(statisticAfterRight)

        withTestActivity { activity ->
            service.rightAnswered(question)
            service.wrongAnswered(question)
        }

        inOrder(mockRepo).apply {
            verify(mockRepo).insertOrUpdate(statisticAfterRight)
            verify(mockRepo).insertOrUpdate(statisticAfterRight.copy(countWrong = 1, lastWrong = TEST_DATE2))
        }
    }

    @Test fun wrongAnswered_insertsNewQuestionStatistic() {
        setDefaultClock()
        withTestActivity { activity ->
            service.wrongAnswered(question)
        }
        verify(mockRepo).insertOrUpdate(QuestionStatistic.testee(id = question.id, countWrong = 1, lastWrong = NOW))
    }

    @Test(expected = IllegalArgumentException::class)
    fun nextQuestion_emptyThrows() {
        whenever(mockLoader.questionsById).thenReturn(emptyMap())
        service.nextQuestion()
    }

    @Test fun nextQuestion_givenOneRightAnsweredAndOneUnanswered_returnsUnanswered() {
        whenMockRepoReadAllReturn(answered.toStatistic())

        assertThat(nextQuestion(answered, unanswered),
                equalTo(unanswered))
    }

    @Test fun nextQuestion_givenTwoRightAnsweredWithDifferentCountRight_returnsOneWithLessCountRight() {
        whenMockRepoReadAllReturn(
                answered1.toStatistic(countRight = 1),
                answered2.toStatistic(countRight = 2))

        assertThat(nextQuestion(answered1, answered2),
                equalTo(answered1))
    }


    @Test fun calcPoints() {
        setDefaultClockDate(NOW)

        assertBiggerPoints("countRight",
                QuestionStatistic.testee(countRight = 0),
                QuestionStatistic.testee(countRight = 1))
        assertBiggerPoints("countWrong",
                QuestionStatistic.testee(countWrong = 1),
                QuestionStatistic.testee(countWrong = 0))
        assertBiggerPoints("lastRight bigger",
                QuestionStatistic.testee(lastRight = NOW.minusDays(2)),
                QuestionStatistic.testee(lastRight = NOW.minusDays(1)))
        assertBiggerPoints("lastRight null",
                QuestionStatistic.testee(lastRight = null),
                QuestionStatistic.testee(lastRight = NOW))
        assertBiggerPoints("lastWrong bigger",
                QuestionStatistic.testee(lastWrong = NOW.minusDays(2)),
                QuestionStatistic.testee(lastWrong = NOW.minusDays(1)))
        assertBiggerPoints("lastWrong null",
                QuestionStatistic.testee(lastWrong = null),
                QuestionStatistic.testee(lastWrong = NOW))
    }

    private fun assertBiggerPoints(message: String, bigger: QuestionStatistic, lower: QuestionStatistic) {
        val service = StatisticServiceImpl(mockRepo, mockLoader, mockClock)
        assertThat("$message => Expected $bigger > $lower", service.calcPoints(bigger),
                greaterThan(service.calcPoints(lower)))
    }

    private fun nextQuestion(vararg questions: Question): Question {
        whenever(mockLoader.questionsById).thenReturn(listOf(*questions).associateBy { it.id })
        return service.nextQuestion()
    }

    private fun whenMockRepoReadAllReturn(vararg questionStatistic: QuestionStatistic) {
        whenever(mockRepo.readAll())
                .thenReturn(listOf(*questionStatistic))
    }

    private fun setDefaultClock(date: String = NOW_STRING) {
        whenever(mockClock.now()).thenReturn(date.parseDateTime())
    }

    private fun setDefaultClockDate(date: Date) {
        whenever(mockClock.now()).thenReturn(date)
    }

}

fun Date.minusDays(days: Int): Date =
        Calendar.getInstance().apply {
            time = this@minusDays
            add(Calendar.DAY_OF_MONTH, -days)
        }.time


private fun Question.toStatistic(countRight: Int = 1) =
        QuestionStatistic(this.id, countRight, 0, null, null)
