package yobu.christophpickl.github.com.yobu


data class Answer(val text: String, val isCorrect: Boolean = false) {
    companion object // for (test) extensions
}

data class Question(
        val id: String,
        val text: String,
        val answers: List<Answer>
) {
    val correctAnswer = answers.first { it.isCorrect }
    val indexOfCorrectAnswer = answers.indexOfFirst { it.isCorrect }

    companion object // for (test) extensions
}
