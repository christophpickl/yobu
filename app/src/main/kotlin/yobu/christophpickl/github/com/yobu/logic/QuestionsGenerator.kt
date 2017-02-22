package yobu.christophpickl.github.com.yobu.logic

import android.support.annotation.VisibleForTesting
import yobu.christophpickl.github.com.yobu.*
import yobu.christophpickl.github.com.yobu.common.prettyPrint

fun main(args: Array<String>) {
    QuestionsGeneratorImpl(RandXImpl)
            .generateDefaultQuestions()
            .filter { it.id.startsWith("BoLocalisation") }
            .prettyPrintQuestions()
}

interface QuestionsGenerator {
    fun generateDefaultQuestions(): List<Question>
}

class QuestionsGeneratorImpl (
        private val randX: RandX
): QuestionsGenerator  {

    private val boPunctDistribution = Distribution(
            BoPunctDistributionItem.values().map { DistributionItem(it.percent, it.meridian) })
    private val boLocalDistribution = Distribution(
            BoLocalisationDistributionGroup.values().map { DistributionItem(it.percent, it) })

    override fun generateDefaultQuestions(): List<Question> {

        //region BO questions
        return BoRelevantMeridian.values().map({ bo ->
            Question(
                    id = "BoPunkt${bo.nameShort}",
                    text = "Bo Punkt von ${bo.nameLong}?",
                    answers = listOf(Answer(bo.boPunct.label, isRight = true))
                            .plus(generateBoPunctAnswers(bo.boPunct))
            )
        })
                .plus(BoRelevantMeridian.values().map { bo ->
                    Question(
                            id = "BoLocalisation${bo.nameShort}",
                            text = "Lage von Bo Punkt für ${bo.nameLong}?",
                            answers = listOf(Answer(bo.localisation, isRight = true))
                                    .plus(generateBoAnswersByMeridianLocalisation(bo))
                    )
                })
                .plus(BoRelevantMeridian.values().map { bo ->
                    Question(
                            id = "BoLocalisation2${bo.nameShort}",
                            text = "Welcher Bo Punkt ist hier lokalisiert: ${bo.localisation}?",
                            answers = listOf(Answer(bo.meridian.nameLong, isRight = true))
                                    .plus(generateBoAnswersByMeridianName(bo))
                    )
                })
                // MINOR question about BO for oberer/mittlerer/unterer erwaermer
                // BO oberer: wo Pk ist
                // BO mittlerer E: wo Ma ist; KG12, 4C A nabel
                // BO unterer: KG7, 1C V nabel

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
        return generateDistinctAnswers(3, except, {
            randX.distributed(boLocalDistribution).randomLocalisation(randX)
        })
    }

    private fun generateBoAnswersByMeridianName(except: BoRelevantMeridian): List<Answer> {
        return generateDistinctAnswers(3, except, { randomBoMeridian(except).nameLong })
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

private enum class BoLocalisationDistributionGroup(val percent: Int) {
    AboveBelly(28) {
        override fun randomLocalisation(randX: RandX): String {
            // 4, 6 correct
            return randX.randomOf(1.rangeTo(8).toList()).toString() + "C cranial vom Nabel"
        }
    },
    BelowBelly(28) {
        override fun randomLocalisation(randX: RandX): String {
            // 2, 3, 4 correct (1 is unterer erwaermer)
            return randX.randomOf(1.rangeTo(6).toList()).toString() + "C caudal vom Nabel"
        }
    },
    SideBelly(5) {
        override fun randomLocalisation(randX: RandX): String {
            // 2 is correct
            return randX.randomOf(1.rangeTo(4).toList()).toString() + "C lateral vom Nabel"
        }
    },
    OnBelly(3) {
        override fun randomLocalisation(randX: RandX): String {
            return "Am Bauchnabel"
        }
    },
    Rips(7) {
        override fun randomLocalisation(randX: RandX): String {
            return "An der " + randX.randomOf(listOf(11, 12)).toString() + ". Rippe"
        }
    },
    Clavicula(3) {
        override fun randomLocalisation(randX: RandX): String {
            return "Unter'm Schlüsselbein"
        }
    },
    ICRRight(14) {
        override fun randomLocalisation(randX: RandX): String {
            return "Im " + randX.randomOf(listOf("6", "7")) + ". ICR"
        }
    },
    ICRWrong(7) {
        override fun randomLocalisation(randX: RandX): String {
            return "Im " + randX.randomOf(listOf("3", "4", "5", "8", "9")) + ". ICR"
        }
    },
    BetweenNipples(5) {
        override fun randomLocalisation(randX: RandX)= "Zwischen den Brustwarzen"
    }
    ;

    abstract fun randomLocalisation(randX: RandX): String
}

private enum class BoPunctDistributionItem(val percent: Int, val meridian: Meridian) {
    KG(60, Meridian.KG),
    Lu(10, Meridian.Lu),
    Ma(10, Meridian.Ma),
    Le(10, Meridian.Le),
    Gb(10, Meridian.Gb)
}

