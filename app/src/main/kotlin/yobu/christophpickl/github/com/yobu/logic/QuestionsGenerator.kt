package yobu.christophpickl.github.com.yobu.logic

import android.support.annotation.VisibleForTesting
import yobu.christophpickl.github.com.yobu.*


class QuestionsGenerator(private val randX: RandX = RandXImpl) {

    private val boPunctDistribution = Distribution(
            BoPunctDistributionItem.values().map { DistributionItem(it.percent, it.meridian) })

    fun generateDefaultQuestions(): List<Question> {

        //region BO questions
        return BoRelevantMeridian.values().map({ bo ->
            Question(
                    id = "BoPunkt${bo.labelShort}",
                    text = "Bo Punkt von ${bo.labelLong}?",
                    answers = listOf(Answer(bo.boPunct.label, isRight = true))
                            .plus(generateBoPunctAnswers(bo.boPunct))
            )
        })
                .plus(BoRelevantMeridian.values().map { bo ->
                    Question(
                            id = "BoLocalisation${bo.labelShort}",
                            text = "Lage von Bo Punkt für ${bo.labelLong}?",
                            answers = listOf(Answer(bo.localisation, isRight = true))
                                    .plus(generateBoAnswersByMeridianLocalisation(bo))
                    )
                })
                .plus(BoRelevantMeridian.values().map { bo ->
                    Question(
                            id = "BoLocalisation2${bo.labelShort}",
                            text = "Welcher Bo Punkt ist hier lokalisiert: ${bo.localisation}?",
                            answers = listOf(Answer(bo.meridian.labelLong, isRight = true))
                                    .plus(generateBoAnswersByMeridianLabel(bo))
                    )
                })

                //endregion

                //region YU questions

                .plus(YuRelevant.values().map { yu ->
                    Question(
                            id = "YuPunkt${yu.labelShort}",
                            text = "Yu Punkt von ${yu.labelLong}?",
                            answers = listOf(Answer(yu.yuPunct.label, isRight = true))
                                    .plus(generateYuPunctAnswers(yu))
                    )
                })
                // TODO "Welcher Yu Punkt auf Bl13"
                .plus(YuRelevant.values().map { yu ->
                    Question(
                            id = "YuLocalisation${yu.labelShort}",
                            text = "Lage vom Yu Punkt für ${yu.labelLong}?",
                            answers = listOf(Answer(yu.localisation, isRight = true))
                                    .plus(generateYuAnswersByMeridianLocalisation(yu))
                    )
                })
                .plus(YuRelevant.values().map { yu ->
                    Question(
                            id = "YuLocalisation2${yu.labelShort}",
                            text = "Welcher Yu Punkt befindet sich am ${yu.localisation}?",
                            answers = listOf(Answer(yu.labelLong, isRight = true))
                                    .plus(generateYuAnswersByMeridianLabel(yu))
                    )
                })

                // MINOR question about oberer/mittlerer/unterer erwaermer

        //endregion

    }

    //region BO stuff

    @VisibleForTesting fun generateBoPunctAnswers(except: PunctCoordinate): List<Answer> {
        return generateDistinctAnswers(3, except, { randomBoPunct(except).label })
    }

    @VisibleForTesting fun randomBoPunct(except: PunctCoordinate): PunctCoordinate {
        val randMeridian = randX.distributed(boPunctDistribution)

        val optionalExceptPoint = if (randMeridian == except.meridian) except.point else null
        val randPoint = randX.randomBetween(1, randMeridian.points, optionalExceptPoint)

        return PunctCoordinate(randMeridian, randPoint)
    }

    private fun generateBoAnswersByMeridianLocalisation(except: BoRelevantMeridian): List<Answer> {
        return generateDistinctAnswers(3, except, { randomBoMeridian(except).localisation })
    }

    private fun generateBoAnswersByMeridianLabel(except: BoRelevantMeridian): List<Answer> {
        return generateDistinctAnswers(3, except, { randomBoMeridian(except).labelLong })
    }

    @VisibleForTesting fun randomBoMeridian(except: BoRelevantMeridian): BoRelevantMeridian {
        return randX.randomOf(BoRelevantMeridian.values(), except)
    }
    //endregion

    //region YU stuff

    private fun generateYuPunctAnswers(except: YuRelevant): List<Answer> {
        return generateDistinctAnswers(3, except, { randomYuPunct(except.yuPunct.point).label })
    }

    private fun randomYuPunct(exceptPoint: Int): PunctCoordinate {
        val randPoint = randX.randomBetween(1, Meridian.Bl.points, exceptPoint)
        return PunctCoordinate(Meridian.Bl, randPoint)
    }

    private fun generateYuAnswersByMeridianLocalisation(except: YuRelevant): List<Answer> {
        return generateDistinctAnswers(3, except, { randomYuMeridian(except).localisation })
    }

    private fun generateYuAnswersByMeridianLabel(except: YuRelevant): List<Answer> {
        return generateDistinctAnswers(3, except, { randomYuMeridian(except).labelLong })
    }

    private fun randomYuMeridian(except: YuRelevant): YuRelevant {
        return randX.randomOf(YuRelevant.valuesArray(), except)
    }

    //endregion

    private fun <T> generateDistinctAnswers(count: Int, except: T, answerText: () -> String): List<Answer> {
        val result = mutableSetOf<Answer>()
        while (result.size != count) {
            // using a Set will ensure there are no duplicates
            result += Answer(answerText())
        }
        return result.toList()
    }
}

private enum class BoPunctDistributionItem(val percent: Int, val meridian: Meridian) {
    KG(60, Meridian.KG),
    Lu(10, Meridian.Lu),
    Ma(10, Meridian.Ma),
    Le(10, Meridian.Le),
    Gb(10, Meridian.Gb)
}
// TODO more and more precise distributions
