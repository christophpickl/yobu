package yobu.christophpickl.github.com.yobu

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class RandomTest {
    @Test
    fun randomBetween_generatesNumbersWithRange() {
        1.rangeTo(100).forEach {
            assertThat(Random.randomBetween(0, 10),
                    allOf(greaterThanOrEqualTo(0), lessThanOrEqualTo(10)))
        }
    }
}