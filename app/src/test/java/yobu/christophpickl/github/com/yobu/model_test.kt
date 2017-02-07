package yobu.christophpickl.github.com.yobu

fun Answer.Companion.testee() = Answer("testText", isRight = true)

fun Question.Companion.testee(
        id: String = "testId",
        text: String = "testText",
        answers: List<Answer> = listOf(Answer.testee())
) = Question(id, text, answers)
