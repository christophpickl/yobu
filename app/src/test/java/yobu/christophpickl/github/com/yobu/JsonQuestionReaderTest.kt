package yobu.christophpickl.github.com.yobu

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class JsonQuestionReaderTest : RobolectricTest() {

    @Test
    fun readJsonFromResources() {
        withTestActivity { activity ->
            val catalog = JsonQuestionReader().read(
                    // TODO read resource from StringReader
                    activity.resources.openRawResource(R.raw.questions_catalog))

            assertThat(catalog, notNullValue())
//            assertThat(catalog.questions, hasSize(2))
//            assertThat(catalog.questions[0].text, equalTo("this is my question 1"))
        }
    }

}
