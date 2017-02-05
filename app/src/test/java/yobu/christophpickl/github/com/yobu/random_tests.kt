package yobu.christophpickl.github.com.yobu

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import yobu.christophpickl.github.com.yobu.logic.BoPunctRandomGenerator
import yobu.christophpickl.github.com.yobu.logic.Meridian
import yobu.christophpickl.github.com.yobu.logic.PunctCoordinate
import yobu.christophpickl.github.com.yobu.misc.Random

class BoPunctRandomGeneratorTest {
    @Test
    fun generate() {
        val except = PunctCoordinate(Meridian.Lu, 1)
        doCoupleOfTimes {
            val randPunct = BoPunctRandomGenerator.generate(except)
            println(randPunct)
            assertThat(randPunct, not(equalTo(except)))
        }
    }
}

class RandomTest {
    @Test
    fun randomBetweenWithoutExcept() {
        doCoupleOfTimes { assertThat(Random.randomBetween(0, 10),
                allOf(greaterThanOrEqualTo(0), lessThanOrEqualTo(10))) }
        doCoupleOfTimes {
            assertThat(Random.randomBetween(50, 99),
                    allOf(greaterThanOrEqualTo(50), lessThanOrEqualTo(99)))
        }
    }
    @Test
    fun randomBetweenWithExcept() {
        doCoupleOfTimes {
            assertThat(Random.randomBetween(0, 10, 5),
                    not(equalTo(5)))
        }
    }

}

private fun doCoupleOfTimes(code: () -> Unit) {
    1.rangeTo(100).forEach { code() }
}