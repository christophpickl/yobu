package yobu.christophpickl.github.com.yobu

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.Test

class JsonQuestionReaderTest : RobolectricTest() {

    @Test
    fun readJsonFromResources() {
        withTestActivity { activity ->
            val catalog = JsonQuestionReader().read(
                    activity.resources.openRawResource(R.raw.questions_catalog))

            assertThat(catalog.questions, hasSize(2))
            assertThat(catalog.questions[0].text, equalTo("this is my question 1"))
        }
    }

}
