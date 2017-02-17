package yobu.christophpickl.github.com.yobu

val Lu1 = PunctCoordinate(Meridian.Lu, 1)
val Ma25 = PunctCoordinate(Meridian.Ma, 25)
val Le13 = PunctCoordinate(Meridian.Le, 13)
val Le14 = PunctCoordinate(Meridian.Le, 14)
val Gb24 = PunctCoordinate(Meridian.Gb, 24)
val Gb25 = PunctCoordinate(Meridian.Gb, 25)

val KG3 = PunctCoordinate(Meridian.KG, 3)
val KG4 = PunctCoordinate(Meridian.KG, 4)
val KG5 = PunctCoordinate(Meridian.KG, 5)
val KG7 = PunctCoordinate(Meridian.KG, 7)
val KG12 = PunctCoordinate(Meridian.KG, 12)
val KG14 = PunctCoordinate(Meridian.KG, 14)
val KG17 = PunctCoordinate(Meridian.KG, 17)

sealed class YuRelevant(
        val labelShort: String,
        val labelLong: String,
        blPoint: Int,
        val localisation: String
) {
    val yuPunct = PunctCoordinate(Meridian.Bl, blPoint)

    @Suppress("LeakingThis")
    val isExtra = this is YuRelevantExtraMeridian

    companion object {
        private val VALUES: List<YuRelevant> = listOf(
                YuRelevantMeridian(Meridian.Lu, 13, "3. BW"),
                YuRelevantMeridian(Meridian.Pk, 14, "4. BW"),
                YuRelevantMeridian(Meridian.He, 15, "5. BW"),
                YuRelevantExtraMeridian("Gv", "Gouverneur", 16, "6. BW"),
                YuRelevantExtraMeridian("Zf", "Zwerchfell", 17, "7. BW"),
                YuRelevantMeridian(Meridian.Le, 18, "9. BW"),
                YuRelevantMeridian(Meridian.Gb, 19, "10. BW"),
                YuRelevantMeridian(Meridian.MP, 20, "11. BW"),
                YuRelevantMeridian(Meridian.Ma, 21, "12. BW"),
                YuRelevantMeridian(Meridian.EEE, 22, "1. LW"),
                YuRelevantMeridian(Meridian.Ni, 23, "2. LW"),
                YuRelevantExtraMeridian("QiMeer", "Meer der Lebensenergie", 24, "3. LW"),
                YuRelevantMeridian(Meridian.Di, 25, "4. LW"),
                YuRelevantExtraMeridian("QiUrspr", "Ursprungsenergie", 26, "5. LW"),
                YuRelevantMeridian(Meridian.Due, 27, "1. KBL"),
                YuRelevantMeridian(Meridian.Bl, 28, "2. KBL")
        )
        private val VALUES_ARRAY = VALUES.toTypedArray()

        fun values() = VALUES
        fun valuesArray() = VALUES_ARRAY
    }

    class YuRelevantMeridian(meridian: Meridian, blPoint: Int, localisation: String)
        : YuRelevant(meridian.labelShort, meridian.labelLong, blPoint, localisation) {

    }

    class YuRelevantExtraMeridian(labelShort: String, labelLong: String, blPoint: Int, localisation: String)
        : YuRelevant(labelShort, labelLong, blPoint, localisation) {

    }
}

enum class BoRelevantMeridian(
        val meridian: MainMeridian,
        val boPunct: PunctCoordinate,
        val localisation: String // this could (should?) actually be part of PunctCoordinate, but would mean optional fields which i dont like, so...!
) : IMeridian by meridian {
    Lu(MainMeridian.Lu, Lu1, "Unter'm Schlüsselbein"),
    Di(MainMeridian.Di, Ma25, "2C lateral vom Nabel"),
    Ma(MainMeridian.Ma, KG12, "4C cranial vom Nabel"),
    MP(MainMeridian.MP, Le13, "An der 11. Rippe"),
    He(MainMeridian.He, KG14, "6C cranial vom Nabel"),
    Due(MainMeridian.Due, KG4, "3C caudal vom Nabel"),
    Bl(MainMeridian.Bl, KG3, "4C caudal vom Nabel"),
    Ni(MainMeridian.Ni, Gb25, "An der 12. Rippe"),
    Pk(MainMeridian.Pk, KG17, "Zwischen den Brustwarzen"),
    EEE(MainMeridian.EEE, KG7, "1C caudal vom Nabel"),
    Gb(MainMeridian.Gb, Gb24, "Im 7. ICR"),
    Le(MainMeridian.Le, Le14, "Im 6. ICR");
}

enum class YuRelevantMeridian(

) : IMeridian

data class PunctCoordinate(val meridian: Meridian, val point: Int) {
    companion object {
        fun parse(string: String): PunctCoordinate {
            val meridian = Meridian.values().firstOrNull { string.startsWith(it.labelShort) } ?: throw IllegalArgumentException("Invalid meridian name: [$string]")
            val number = string.substring(meridian.labelShort.length).toInt()
            if (number < 1 || number > meridian.points) throw IllegalArgumentException("Invalid point number: $number for meridian $meridian")
            return PunctCoordinate(meridian, number)
        }
    }

    val label = meridian.labelShort + point
//    fun toNiceString() = meridian.text + point
}

enum class MainMeridian(meridian: Meridian) : IMeridian by meridian {
    Lu(Meridian.Lu),
    Di(Meridian.Di),
    Ma(Meridian.Ma),
    MP(Meridian.MP),
    He(Meridian.He),
    Due(Meridian.Due),
    Bl(Meridian.Bl),
    Ni(Meridian.Ni),
    Pk(Meridian.Pk),
    EEE(Meridian.EEE),
    Gb(Meridian.Gb),
    Le(Meridian.Le);

    companion object {
        val size by lazy { MainMeridian.values().size }
    }
}

interface IMeridian {
    val labelShort: String
    val labelLong: String
    val points: Int
}

enum class Meridian(
        override val labelShort: String,
        override val labelLong: String,
        override val points: Int
) : IMeridian {
    Lu("Lu", "Lunge", 11),
    Di("Di", "Dickdarm", 20),
    Ma("Ma", "Magen", 45),
    MP("MP", "Milz", 21),
    He("He", "Herz", 9),
    Due("Due", "Dünndarm", 19),
    Bl("Bl", "Blase", 67),
    Ni("Ni", "Niere", 27),
    Pk("Pk", "Perikard", 9),
    EEE("3E", "3fach Erwärmer", 23),
    Gb("Gb", "Gallenblase", 44),
    Le("Le", "Leber", 14),
    //    LG("LG", "Du Mai", 28),
    KG("KG", "Ren Mai", 24)
}

data class Answer(
        val text: String,
        val isRight: Boolean = false
) {
    companion object // for (test) extensions
}

data class Question(
        val id: String,
        val text: String,
        val answers: List<Answer>
) {
    val rightAnswers = answers.filter { it.isRight }
    val indicesOfRightAnswers = rightAnswers.mapIndexed { i, answer -> i }

    companion object // for (test) extensions
}
