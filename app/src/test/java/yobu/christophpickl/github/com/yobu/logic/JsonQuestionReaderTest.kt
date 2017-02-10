package yobu.christophpickl.github.com.yobu.logic

import org.apache.tools.ant.filters.StringInputStream
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test
import yobu.christophpickl.github.com.yobu.testinfra.RobolectricTest
import yobu.christophpickl.github.com.yobu.logic.JsonQuestionReader
import yobu.christophpickl.github.com.yobu.logic.QuestionFlag

class JsonQuestionReaderTest : RobolectricTest() {

    @Test
    fun readFull() {
        withTestActivity { activity ->
            val catalog = JsonQuestionReader().read(StringInputStream("""
                {
                  "questions": [
                    {
                      "id": "testId",
                      "text": "testText",
                      "flags": [],
                      "answers": [
                        {
                          "text": "Lu1"
                        }
                      ]
                    }
                  ]
                }
            """))
            assertThat(catalog.questions, hasSize(1))
            assertThat(catalog.questions[0].id, equalTo("testId"))
            assertThat(catalog.questions[0].text, equalTo("testText"))
//            assertThat(catalog.questions[0].flags, contains(QuestionFlag.RANDOM_BO_PUNCT))
            assertThat(catalog.questions[0].answers, hasSize(1))
            assertThat(catalog.questions[0].answers[0].text, equalTo("Lu1"))
        }
    }

    @Test
    fun readWithoutFlags() {
        withTestActivity { activity ->
            val catalog = JsonQuestionReader().read(StringInputStream("""
                {
                  "questions": [
                    {
                      "id": "testId",
                      "text": "Der Bo Punkt von Lunge?",
                      "answers": [
                        {
                          "text": "Lu1"
                        }
                      ]
                    }
                  ]
                }
            """))
            assertThat(catalog.questions[0].flags, empty())
        }
    }

}
