package yobu.christophpickl.github.com.yobu

class QuestionRepo(private val questions: List<Question>) {

    private var index = 0

    fun nextQuestion(): Question {
        // randomize elements

        val question = questions[index]
        index++
        if (index >= questions.size) {
            index = 0
        }
        return question
    }

}
