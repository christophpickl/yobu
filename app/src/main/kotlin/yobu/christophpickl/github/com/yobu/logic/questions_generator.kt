package yobu.christophpickl.github.com.yobu.logic

import android.support.annotation.VisibleForTesting
import yobu.christophpickl.github.com.yobu.*


class BoPunctGenerator(private val randX: RandX = RandXImpl) {

    private val boPunctDistribution = Distribution(BoPunctDistributionItem.values().map { DistributionItem(it.percent, it.meridian) })

    fun generateDefaultQuestions(): List<Question> {
//        // TODO generate the random answers each time question is displayed!
        // generate questions asking for Bo POINTS
        return BoRelevantMeridian.values().map({ boMeridian ->
            Question(
                    id = "BoPunkt${boMeridian.labelShort}",
                    text = "Bo Punkt von ${boMeridian.labelLong}?",
                    answers = listOf(Answer(boMeridian.boPunct.label, isRight = true))
                            .plus(randomBoPunctAnswers(boMeridian.boPunct))
            )
            // generate questions asking for Bo LOCALISATION texts
        }).plus(BoRelevantMeridian.values().map { boMeridian ->
            Question(
                    id = "BoLocalisation${boMeridian.labelShort}",
                    text = "Lage von Bo Punkt für ${boMeridian.labelLong}?",
                    answers = listOf(Answer(boMeridian.localisation, isRight = true))
                            .plus(randomBoLocalisationAnswers(boMeridian))
            )
        })
                // generation questions asking for Yu POINTS
                .plus(YuRelevant.values().map { yu ->
                    Question(
                            id = "YuPunkt${yu.labelShort}",
                            text = "Yu Punkt von ${yu.labelLong}?",
                            answers = listOf(Answer(yu.yuPunct.label, isRight = true))
                                    .plus(randomYuPunctAnswers(yu))
                    )
                })
                .plus(YuRelevant.values().map { yu ->
                    Question(
                            id = "YuLocalisation${yu.labelShort}",
                            text = "Lage von Yu für ${yu.labelLong}?",
                            answers = listOf(Answer(yu.localisation, isRight = true))
                                    .plus(randomYuLocalisationAnswers(yu))
                    )
                })
    }

    private fun randomBoPunctAnswers(except: PunctCoordinate): List<Answer> {
        // FIXME when generating 1..3, each time the except list should grow (see also down below)
        return 1.rangeTo(3).map {
            Answer(randomBoPunct(except).label)
        }
    }

    // FIXME when generating 1..3, each time the except list should grow (see also down below)
    private fun randomYuPunctAnswers(except: YuRelevant): List<Answer> {
        return 1.rangeTo(3).map {
            Answer(randomYuPunct(except.yuPunct.point).label)
        }
    }

    private fun randomYuLocalisationAnswers(except: YuRelevant): List<Answer> {
        return 1.rangeTo(3).map {
            Answer(randomYuMeridian(except).localisation)
        }
    }

    private fun randomYuMeridian(except: YuRelevant): YuRelevant {
        return randX.randomOf(YuRelevant.valuesArray(), except)
    }

    private fun randomBoLocalisationAnswers(except: BoRelevantMeridian): List<Answer> {
        return 1.rangeTo(3).map {
            Answer(randomBoMeridian(except).localisation)
        }
    }

    @VisibleForTesting fun randomBoPunct(except: PunctCoordinate): PunctCoordinate {
        val randMeridian = randX.distributed(boPunctDistribution)

        val optionalExceptPoint = if (randMeridian == except.meridian) except.point else null
        val randPoint = randX.randomBetween(1, randMeridian.points, optionalExceptPoint)

        return PunctCoordinate(randMeridian, randPoint)
    }

    private fun randomYuPunct(exceptPoint: Int): PunctCoordinate {
        val randPoint = randX.randomBetween(1, Meridian.Bl.points, exceptPoint)
        return PunctCoordinate(Meridian.Bl, randPoint)
    }

    @VisibleForTesting fun randomBoMeridian(except: BoRelevantMeridian): BoRelevantMeridian {
        return randX.randomOf(BoRelevantMeridian.values(), except)
    }
}

private enum class BoPunctDistributionItem(val percent: Int, val meridian: Meridian) {
    KG(60, Meridian.KG),
    Lu(10, Meridian.Lu),
    Ma(10, Meridian.Ma),
    Le(10, Meridian.Le),
    Gb(10, Meridian.Gb)
    // TODO special type: use same meridian as "except" instance but different point
}