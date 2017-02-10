package yobu.christophpickl.github.com.yobu.logic

import android.support.annotation.VisibleForTesting
import yobu.christophpickl.github.com.yobu.*


class BoPunctGenerator(private val randX: RandX = RandXImpl) {
    //    {
//        "id": "BoPunktLu",
//        "text": "Bo Punkt von Lunge?",
//        "flags": [ "RANDOM_BO_PUNCT" ],
//        "answers": [
//        {
//            "text": "Lu1"
//        }
//        ]
//    }
    private val punctDistribution = Distribution(BoPunctDistributionItem.values().map { DistributionItem(it.percent, it.meridian) })

    fun generateDefaultQuestions(): List<Question> {
        // generate questions asking for Bo POINTS
        return BoRelevantMeridian.values().map { boMeridian ->
            Question(
                    id = "BoPunkt${boMeridian.labelShort}",
                    text = "Bo Punkt von ${boMeridian.labelLong}?",
                    answers = listOf(Answer(boMeridian.boPunct.label, isRight = true))
                            // TODO generate the random answers each time question is displayed! (as well as for localisation)
                            .plus(randomPunctAnswers(boMeridian.boPunct))
            )
        // generate questions asking for Bo LOCALISATION texts
        }.plus(BoRelevantMeridian.values().map { boMeridian ->
            Question(
                    id = "BoLocalisation${boMeridian.labelShort}",
                    text = "Lage von Bo Punkt für ${boMeridian.labelLong}?",
                    answers = listOf(Answer(boMeridian.localisation, isRight = true))
                            .plus(randomLocalisationAnswers(boMeridian))
            )
        })
    }

    private fun randomPunctAnswers(except: PunctCoordinate): List<Answer> {
        // FIXME when generating 1..3, each time the except list should grow (see also down below)
        return 1.rangeTo(3).map {
            Answer(randomPunct(except).label)
        }
    }

    private fun randomLocalisationAnswers(except: BoRelevantMeridian): List<Answer> {
        return 1.rangeTo(3).map {
            Answer(randomBoMeridian(except).localisation)
        }
    }

    @VisibleForTesting fun randomPunct(except: PunctCoordinate): PunctCoordinate {
        val randMeridian = randX.distributed(punctDistribution)

        val optionalExceptPoint = if(randMeridian == except.meridian) except.point else null
        val randPoint = randX.randomBetween(1, randMeridian.points, optionalExceptPoint)

        return PunctCoordinate(randMeridian, randPoint)
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