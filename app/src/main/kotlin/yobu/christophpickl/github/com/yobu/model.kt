package yobu.christophpickl.github.com.yobu


data class Answer(val text: String, val isRight: Boolean = false) {
    companion object // for (test) extensions
}

data class Question(
        val id: String,
        val text: String,
        val answers: List<Answer>
) {
    val rightAnswer = answers.first { it.isRight }
    val indexOfRightAnswer = answers.indexOfFirst { it.isRight }

    companion object // for (test) extensions
}
