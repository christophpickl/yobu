package yobu.christophpickl.github.com.yobu.logic

import android.support.annotation.VisibleForTesting
import yobu.christophpickl.github.com.yobu.*
import yobu.christophpickl.github.com.yobu.common.prettyPrint

fun main(args: Array<String>) {
    QuestionsGeneratorImpl(RandXImpl)
            .generateDefaultQuestions()
//            .filter { it.id.startsWith("BoPunkt") }
            .prettyPrintQuestions()
}

interface QuestionsGenerator {
    fun generateDefaultQuestions(): List<Question>
}

class QuestionsGeneratorImpl(
        private val randX: RandX
) : QuestionsGenerator {

//    private val boPunctDistribution = Distribution(
//            BoPunctDistributionItem.values().map { DistributionItem(it.percent, it) })
//    private val boLocalDistribution = Distribution(
//            BoLocalisationDistributionGroup.values().map { DistributionItem(it.percent, it) })

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
                            text = "Bo Lage von: \"${bo.localisation}\"?",
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
        val distribution = randX.distributed(BoPunctDistributionItem.Distribution)
        return distribution.randomLocalisation(randX)
    }

    private fun generateBoAnswersByMeridianLocalisation(except: BoRelevantMeridian): List<Answer> {
        return generateDistinctAnswers(3, except, {
            randX.distributed(BoLocalisationDistributionGroup.Distribution).randomLocalisation(randX)
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
        val randPoint = randX.randomBetween(11, 28, exceptPoint)
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
        override fun randomLocalisation(randX: RandX) = "Zwischen den Brustwarzen"
    }
    ;

    companion object {
        val Distribution = Distribution(BoLocalisationDistributionGroup.values().map { DistributionItem(it.percent, it) })
    }

    abstract fun randomLocalisation(randX: RandX): String
}

// right: 3, 4, 5, 12, 14, 17
private val allRightKGBoPoints = BoRelevantMeridian.values().filter { it.boPunct.meridian == Meridian.KG }.map { it.boPunct.point }
private enum class BoPunctDistributionItem(val percent: Int, val meridian: Meridian) {
    KGRight(40, Meridian.KG) {
        override fun randomLocalisation(randX: RandX) = PunctCoordinate(meridian, randX.randomOf(allRightKGBoPoints))
    },
    KGWrong(10, Meridian.KG) {
        override fun randomLocalisation(randX: RandX) = PunctCoordinate(meridian, randX.randomOf(1.rangeTo(19).toList().minus(allRightKGBoPoints)))
    },
    Lu(3, Meridian.Lu) {
        override fun randomLocalisation(randX: RandX) = Lu1
    },
    MaRight(5, Meridian.Ma) {
        override fun randomLocalisation(randX: RandX) = Ma25
    },
    MaWrong(2, Meridian.Ma) {
        override fun randomLocalisation(randX: RandX) = PunctCoordinate(meridian, randX.randomOf(listOf(2, 5, 15, 20, 22)))
    },
    LeRight(14, Meridian.Le) {
        override fun randomLocalisation(randX: RandX) = PunctCoordinate(meridian, randX.randomOf(listOf(13, 14)))
    },
    LeWrong(6, Meridian.Le) {
        override fun randomLocalisation(randX: RandX) = PunctCoordinate(meridian, randX.randomOf(listOf(10, 11, 12, 15, 16, 17, 18, 19)))
    },
    GbRight(14, Meridian.Gb) {
        override fun randomLocalisation(randX: RandX) = PunctCoordinate(meridian, randX.randomOf(listOf(24, 25)))
    },
    GbWrong(6, Meridian.Gb) {
        override fun randomLocalisation(randX: RandX) = PunctCoordinate(meridian, randX.randomOf(listOf(20, 21, 22, 23, 26, 27, 28, 29)))
    }
    ;

    companion object {
        val Distribution = Distribution(BoPunctDistributionItem.values().map { DistributionItem(it.percent, it) })
    }

    abstract fun randomLocalisation(randX: RandX): PunctCoordinate
}

