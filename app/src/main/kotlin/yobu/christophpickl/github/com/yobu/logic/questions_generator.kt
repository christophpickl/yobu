package yobu.christophpickl.github.com.yobu.logic

import android.support.annotation.VisibleForTesting
import yobu.christophpickl.github.com.yobu.*


class BoPunctGenerator(private val randX: RandX = RandXImpl) {

    private val boPunctDistribution = Distribution(BoPunctDistributionItem.values().map { DistributionItem(it.percent, it.meridian) })

    fun generateDefaultQuestions(): List<Question> {
        // generate questions asking for Bo POINTS
        return BoRelevantMeridian.values().map({ boMeridian ->
            Question(
                    id = "BoPunkt${boMeridian.labelShort}",
                    text = "Bo Punkt von ${boMeridian.labelLong}?",
                    answers = listOf(Answer(boMeridian.boPunct.label, isRight = true))
                            .plus(generatePoPunctAnswers(boMeridian.boPunct))
            )
            // generate questions asking for Bo LOCALISATION texts
        }).plus(BoRelevantMeridian.values().map { boMeridian ->
            Question(
                    id = "BoLocalisation${boMeridian.labelShort}",
                    text = "Lage von Bo Punkt für ${boMeridian.labelLong}?",
                    answers = listOf(Answer(boMeridian.localisation, isRight = true))
                            .plus(generateBoLocalisationAnswers(boMeridian))
            )
        })
                // generation questions asking for Yu POINTS
                .plus(YuRelevant.values().map { yu ->
                    Question(
                            id = "YuPunkt${yu.labelShort}",
                            text = "Yu Punkt von ${yu.labelLong}?",
                            answers = listOf(Answer(yu.yuPunct.label, isRight = true))
                                    .plus(generateYuPunctAnswers(yu))
                    )
                })
                .plus(YuRelevant.values().map { yu ->
                    Question(
                            id = "YuLocalisation${yu.labelShort}",
                            text = "Lage von Yu für ${yu.labelLong}?",
                            answers = listOf(Answer(yu.localisation, isRight = true))
                                    .plus(generateYuLocalisationAnswers(yu))
                    )
                })
    }

    //region BO stuff

    @VisibleForTesting fun generatePoPunctAnswers(except: PunctCoordinate): List<Answer> {
        return generateDistinctAnswers(3, except, { randomBoPunct(except).label })
    }

    @VisibleForTesting fun randomBoPunct(except: PunctCoordinate): PunctCoordinate {
        val randMeridian = randX.distributed(boPunctDistribution)

        val optionalExceptPoint = if (randMeridian == except.meridian) except.point else null
        val randPoint = randX.randomBetween(1, randMeridian.points, optionalExceptPoint)

        return PunctCoordinate(randMeridian, randPoint)
    }

    private fun generateBoLocalisationAnswers(except: BoRelevantMeridian): List<Answer> {
        return generateDistinctAnswers(3, except, { randomBoMeridian(except).localisation })
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

    private fun generateYuLocalisationAnswers(except: YuRelevant): List<Answer> {
        return generateDistinctAnswers(3, except, { randomYuMeridian(except).localisation })
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