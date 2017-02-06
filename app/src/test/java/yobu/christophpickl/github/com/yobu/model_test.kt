package yobu.christophpickl.github.com.yobu

fun Answer.Companion.testee() = Answer("testText", isCorrect = true)

fun Question.Companion.testee() = Question("testId", "testText", listOf(Answer.testee()))
