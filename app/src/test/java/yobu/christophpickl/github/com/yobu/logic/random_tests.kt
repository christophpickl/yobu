package yobu.christophpickl.github.com.yobu.logic

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class BoPunctRandomGeneratorTest {
    @Test
    fun generate() {
        val except = PunctCoordinate(Meridian.Lu, 1)
        doCoupleOfTimes {
            val randPunct = BoPunctRandomGenerator.generate(except)
            assertThat(randPunct, not(equalTo(except)))
        }
    }
}

class RandomTest {
    @Test
    fun randomBetweenWithoutExcept() {
        doCoupleOfTimes {
            assertThat(Random.randomBetween(0, 10),
                    allOf(greaterThanOrEqualTo(0), lessThanOrEqualTo(10)))
        }
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

    @Test
    fun distributedRandom_100PercentAlwaysReturnsSameItem() {
        doCoupleOfTimes {
            assertThat(Random.distributed(distributionOf(100 to "a")),
                    equalTo("a"))
        }
    }

    @Test
    fun _distributedRandom() {
        val distribution = distributionOf(80 to "a", 20 to "b")
        assertThat(Random._distributed(distribution, 0), equalTo("a"))
        assertThat(Random._distributed(distribution, 50), equalTo("a"))
        assertThat(Random._distributed(distribution, 80), equalTo("a"))
        assertThat(Random._distributed(distribution, 81), equalTo("b"))
        assertThat(Random._distributed(distribution, 100), equalTo("b"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun distributedWrongTooLess() {
        Random.distributed(distributionOf(99 to ""))
    }
    @Test(expected = IllegalArgumentException::class)
    fun distributedWrongTooMuch() {
        Random.distributed(distributionOf(100 to "", 1 to ""))
    }

}


private fun doCoupleOfTimes(code: () -> Unit) {
    1.rangeTo(100).forEach { code() }
}