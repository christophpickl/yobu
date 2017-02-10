package yobu.christophpickl.github.com.yobu.logic

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
    private val distribution = Distribution(BoDistributionItem.values().map { DistributionItem(it.percent, it.meridian) })

    fun generate(except: PunctCoordinate): PunctCoordinate {
        val randMeridian = randX.distributed(distribution)

        val exceptPoint = if(randMeridian == except.meridian) except.point else null
        val randPoint = randX.randomBetween(1, randMeridian.points, exceptPoint)

        return PunctCoordinate(randMeridian, randPoint)
    }

    fun generateRandomAnswers(except: PunctCoordinate): List<Answer> {
        return 1.rangeTo(3).map {
            Answer(generate(except).label)
        }
    }

    fun generateDefaultQuestions(): List<Question> {
        return BoRelevantMeridian.values().map { boMeridian ->
            Question(
                    id = "BoPunkt${boMeridian.labelShort}",
                    text = "Bo Punkt von ${boMeridian.labelLong}?",
                    answers = listOf(Answer(boMeridian.boPunct.label, isRight = true))
                            // TODO generate answers each time question is displayed!
                            .plus(generateRandomAnswers(boMeridian.boPunct))
            )
        }.plus(BoRelevantMeridian.values().map { boMeridian ->
            Question(
                    id = "BoLocalisation${boMeridian.labelShort}",
                    text = "Bo Punkt von ${boMeridian.labelLong}?",
                    answers = listOf(Answer(boMeridian.boPunct.label, isRight = true))
                            // TODO generate answers each time question is displayed!
                            .plus(generateRandomAnswers(boMeridian.boPunct))
            )
        })
    }
}

private enum class BoDistributionItem(val percent: Int, val meridian: Meridian) {
    KG(60, Meridian.KG),
    Lu(10, Meridian.Lu),
    Ma(10, Meridian.Ma),
    Le(10, Meridian.Le),
    Gb(10, Meridian.Gb)
    // TODO special type: use same meridian as "except" instance but different point
}