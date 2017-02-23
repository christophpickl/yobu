package yobu.christophpickl.github.com.yobu.logic

import yobu.christophpickl.github.com.yobu.common.Clock
import yobu.christophpickl.github.com.yobu.common.parseDateTime
import yobu.christophpickl.github.com.yobu.logic.persistence.testee
import java.text.SimpleDateFormat
import java.util.*


class StatApp {

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")

    @Test fun printStatPointsSummary() {
        val today = "2017-01-20 00:21:42".parseDateTime()
        val yesterday1 = today.minusDays(1)
        val yesterday10 = today.minusDays(10)
        val mockClock = mock<Clock>()
        whenever(mockClock.now()).thenReturn(today)

        val service = StatisticServiceImpl(mock<QuestionStatisticsRepository>(), mock<QuestionLoader>(), mockClock)

        println("(now: ${DATE_FORMAT.format(today)})")
        println("%7s  %4s  %4s  %10s  %10s".format("PTS", "#OK", "#KO", "Last OK", "Last KO"))
        println("-------------------------------------------")
        listOf(
                QuestionStatistic.testee()
                , QuestionStatistic.testee(countRight = 10, lastRight = today)
                , QuestionStatistic.testee(countRight = 1, lastRight = today)
                , QuestionStatistic.testee(countRight = 1, lastRight = yesterday1)
                , QuestionStatistic.testee(countRight = 1, lastRight = yesterday10)

                , QuestionStatistic.testee(countWrong = 10, lastWrong = today)
                , QuestionStatistic.testee(countWrong = 1, lastWrong = today)
                , QuestionStatistic.testee(countWrong = 1, lastWrong = yesterday1)
                , QuestionStatistic.testee(countWrong = 1, lastWrong = yesterday10)
                , QuestionStatistic.testee(
                countRight = 1, lastRight = today.minusDays(1),
                countWrong = 1, lastWrong = today.minusDays(1))
                , QuestionStatistic.testee(
                countRight = 1, lastRight = today.minusDays(1),
                countWrong = 10, lastWrong = today.minusDays(2))
                , QuestionStatistic.testee(
                countRight = 10, lastRight = today.minusDays(2),
                countWrong = 1, lastWrong = today.minusDays(1))
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