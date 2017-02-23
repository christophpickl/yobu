package yobu.christophpickl.github.com.yobu.logic

import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.should.shouldNotMatch
import org.junit.Before
import org.junit.Test
import yobu.christophpickl.github.com.yobu.Lu1

class QuestionsGeneratorTest {

    private val anyPunct = Lu1

    private lateinit var generator: QuestionsGeneratorImpl

    @Before fun setup() {
        generator = QuestionsGeneratorImpl(RandXImpl)
    }

    @Test fun randomBoPunct_shouldNotReturnTheExceptPunct() {
        doCoupleOfTimes {
            // MINOR TEST inject mock
            generator.randomBoPunct(except = anyPunct) shouldNotMatch equalTo(anyPunct)
        }
    }

    @Test fun generatePoPunctAnswers_shouldNotReturnDuplicates() {
        doCoupleOfTimes {
            generator.generateBoPunctAnswers(anyPunct)
                    .assertDistinctItems()
        }
    }

    private fun <E> List<E>.assertDistinctItems() {
        assertThat("list: $this", distinct().size, equalTo(size)) // MINOR TEST how to get hamkrest's hasSize working?!
    }
    /*

    @Test
    fun generate() {
        val except = PunctCoordinate(Meridian.Lu, 1)
        doCoupleOfTimes {
            // could inject mock and test more precisely
            val randPunct = generator.randomBoPunct(except)
            MatcherAssert.assertThat(randPunct, Matchers.not(Matchers.equalTo(except)))
        }
    }

    @Test fun generateDefaultQuestions_forEachMainMeridianGeneratesPunctAndLocalisationQuestion() {
        withTestActivity { activity ->
            val questions = generator.generateDefaultQuestions()
            println(questions.joinToString("\n"))
            MatcherAssert.assertThat(questions, Matchers.hasSize(MainMeridian.size * 2))
        }
    }
     */
}


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
                    greaterThanOrEqualTo(0) and lessThanOrEqualTo(10))
        }
        doCoupleOfTimes {
            assertThat(RandXImpl.randomBetween(50, 99),
                    greaterThanOrEqualTo(50) and lessThanOrEqualTo(99))
        }
    }

    @Test
    fun randomBetweenWithExcept() {
        doCoupleOfTimes {
            RandXImpl.randomBetween(0, 10, except = 5) shouldNotMatch equalTo(5)
        }
    }

    @Test fun randomElementsExcept() {
        val abc = listOf("a", "b", "c")
        assertThat(RandXImpl.randomElementsExcept(abc, 2, "a"),
                hasElement("b") and hasElement("c")
        )

        assertThat(RandXImpl.randomElementsExcept(abc, 1, "a"),
                hasElement("b") or hasElement("c")
        )
    }

}


private fun doCoupleOfTimes(code: () -> Unit) {
    1.rangeTo(100).forEach { code() }
}