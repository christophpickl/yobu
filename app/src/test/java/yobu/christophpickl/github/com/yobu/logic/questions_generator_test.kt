package yobu.christophpickl.github.com.yobu.logic

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Test
import yobu.christophpickl.github.com.yobu.MainMeridian
import yobu.christophpickl.github.com.yobu.Meridian
import yobu.christophpickl.github.com.yobu.PunctCoordinate
import yobu.christophpickl.github.com.yobu.testinfra.RobolectricTest
import yobu.christophpickl.github.com.yobu.testinfra.doCoupleOfTimes


class BoPunctGeneratorTest : RobolectricTest() {

    @Test
    fun generate() {
        val except = PunctCoordinate(Meridian.Lu, 1)
        doCoupleOfTimes {
            // could inject mock and test more precisely
            val randPunct = BoPunctGenerator().randomBoPunct(except)
            MatcherAssert.assertThat(randPunct, Matchers.not(Matchers.equalTo(except)))
        }
    }

    @Test fun generateDefaultQuestions_forEachMainMeridianGeneratesPunctAndLocalisationQuestion() {
        withTestActivity { activity ->
            val questions = BoPunctGenerator().generateDefaultQuestions()
            println(questions.joinToString("\n"))
            MatcherAssert.assertThat(questions, Matchers.hasSize(MainMeridian.size * 2))
        }
    }
}
