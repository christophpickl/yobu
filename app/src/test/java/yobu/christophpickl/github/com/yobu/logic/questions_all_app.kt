package yobu.christophpickl.github.com.yobu.logic

import org.junit.Test
import yobu.christophpickl.github.com.yobu.prettyPrintQuestions

class QuestionsAllAppNonTest {

    @Test fun printAllGeneratedQuestions() {
        QuestionsGeneratorImpl(RandXImpl).generateDefaultQuestions().prettyPrintQuestions()
    }

}
