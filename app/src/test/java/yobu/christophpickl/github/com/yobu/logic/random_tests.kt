package yobu.christophpickl.github.com.yobu.logic

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import yobu.christophpickl.github.com.yobu.MainMeridian
import yobu.christophpickl.github.com.yobu.Meridian
import yobu.christophpickl.github.com.yobu.PunctCoordinate
import yobu.christophpickl.github.com.yobu.testinfra.RobolectricTest
import yobu.christophpickl.github.com.yobu.testinfra.doCoupleOfTimes


class RandXImplTest {

    @Test
    fun distributedRandom_100PercentAlwaysReturnsSameItem() {
        doCoupleOfTimes {
            assertThat(RandXImpl.distributed(distributionOf(100 to "a")),
                    equalTo("a"))
        }
    }

    @Test
    fun _distributedRandom() {
        val distribution = distributionOf(80 to "a", 20 to "b")
        assertThat(RandXImpl._distributed(distribution, 0), equalTo("a"))
        assertThat(RandXImpl._distributed(distribution, 50), equalTo("a"))
        assertThat(RandXImpl._distributed(distribution, 80), equalTo("a"))
        assertThat(RandXImpl._distributed(distribution, 81), equalTo("b"))
        assertThat(RandXImpl._distributed(distribution, 100), equalTo("b"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun distributedWrongTooLess() {
        RandXImpl.distributed(distributionOf(99 to ""))
    }
    @Test(expected = IllegalArgumentException::class)
    fun distributedWrongTooMuch() {
        RandXImpl.distributed(distributionOf(100 to "", 1 to ""))
    }

    @Test
    fun randomBetweenWithoutExcept() {
        doCoupleOfTimes {
            assertThat(RandXImpl.randomBetween(0, 10),
                    allOf(greaterThanOrEqualTo(0), lessThanOrEqualTo(10)))
        }
        doCoupleOfTimes {
            assertThat(RandXImpl.randomBetween(50, 99),
                    allOf(greaterThanOrEqualTo(50), lessThanOrEqualTo(99)))
        }
    }

    @Test
    fun randomBetweenWithExcept() {
        doCoupleOfTimes {
            assertThat(RandXImpl.randomBetween(0, 10, 5),
                    not(equalTo(5)))
        }
    }
}
