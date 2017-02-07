package yobu.christophpickl.github.com.yobu.logic

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import yobu.christophpickl.github.com.yobu.logic.persistence.testee
import yobu.christophpickl.github.com.yobu.misc.Clock
import yobu.christophpickl.github.com.yobu.misc.formatDateTime
import yobu.christophpickl.github.com.yobu.misc.parseDateTime
import java.text.SimpleDateFormat
import java.util.*


class StatApp {

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")

    @Test fun printStatPointsSummary() {
        val today = "2017-01-10 00:21:42".parseDateTime()
        val yesterday1 = today.minusDays(1)
        val yesterday2 = today.minusDays(2)
        val mockClock = mock<Clock>()
        whenever(mockClock.now()).thenReturn(today)

        val service = QuestionStatisticService(mock<QuestionStatisticsRepository>(), mockClock)

        println("(now = ${today.formatDateTime()})")
        println("%7s  %4s  %4s  %10s  %10s".format("PTS", "#OK", "#KO", "Last OK", "Last KO"))
        println("-------------------------------------------")
        listOf(
                QuestionStatistic.testee()
                , QuestionStatistic.testee(countRight = 1, lastRight = yesterday1)
                , QuestionStatistic.testee(countWrong = 1, lastRight = yesterday1)
                , QuestionStatistic.testee(
                countRight = 1, lastRight = today.minusDays(1),
                countWrong = 2, lastWrong = today.minusDays(2))


//                , QuestionStatistic.testee(countRight = 1)
//                , QuestionStatistic.testee(countRight = 2)
//                , QuestionStatistic.testee(countWrong = 1)
//                , QuestionStatistic.testee(countWrong = 2)
//                , QuestionStatistic.testee(lastRight = today)
//                , QuestionStatistic.testee(lastRight = yesterday1)
//                , QuestionStatistic.testee(lastRight = yesterday2)
//                , QuestionStatistic.testee(lastWrong = today)
//                , QuestionStatistic.testee(lastWrong = yesterday1)
//                , QuestionStatistic.testee(lastWrong = yesterday2)

        )
                .map { Pair(service.calcPoints(it), it) }
                .sortedByDescending { it.first }
                .forEach {
                    val (points, stats) = it
                    println("%7s  %4s  %4s  %10s  %10s".format(points, stats.countRight, stats.countWrong,
                            formatDate(stats.lastRight), formatDate(stats.lastWrong)))
                }
    }

    private fun formatDate(date: Date?) = if (date == null) "--" else DATE_FORMAT.format(date)
}