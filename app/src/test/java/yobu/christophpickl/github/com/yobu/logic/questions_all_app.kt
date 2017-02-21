package yobu.christophpickl.github.com.yobu.logic

import org.junit.Test
import yobu.christophpickl.github.com.yobu.Question

class QuestionsAllAppNonTest {

    @Test fun printAllGeneratedQuestions() {
        val questions = QuestionsGenerator().generateDefaultQuestions()
        println(questions.map { it.prettyPrintMe() }.joinToString("\n"))
    }

}

fun Question.prettyPrintMe() =
        "- $id => $text\n" +
                answers.map { "\t* ${if(it.isRight) "! " else ""}${it.text}" }
                        .joinToString("\n")