package yobu.christophpickl.github.com.yobu


data class Answer(val text: String, val isCorrect: Boolean = false)

data class Question(
        val text: String,
        val answers: List<Answer>
) {
    val correctAnswer = answers.first { it.isCorrect }
    val indexOfCorrectAnswer = answers.indexOfFirst { it.isCorrect }
}
