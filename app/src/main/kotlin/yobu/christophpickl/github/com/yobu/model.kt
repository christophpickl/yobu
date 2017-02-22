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

val Bl13 = PunctCoordinate(Meridian.Bl, 13)
val Bl14 = PunctCoordinate(Meridian.Bl, 14)
val Bl15 = PunctCoordinate(Meridian.Bl, 15)
val Bl16 = PunctCoordinate(Meridian.Bl, 16)
val Bl17 = PunctCoordinate(Meridian.Bl, 17)
val Bl18 = PunctCoordinate(Meridian.Bl, 18)
val Bl19 = PunctCoordinate(Meridian.Bl, 19)
val Bl20 = PunctCoordinate(Meridian.Bl, 20)
val Bl21 = PunctCoordinate(Meridian.Bl, 21)
val Bl22 = PunctCoordinate(Meridian.Bl, 22)
val Bl23 = PunctCoordinate(Meridian.Bl, 23)
val Bl24 = PunctCoordinate(Meridian.Bl, 24)
val Bl25 = PunctCoordinate(Meridian.Bl, 25)
val Bl26 = PunctCoordinate(Meridian.Bl, 26)
val Bl27 = PunctCoordinate(Meridian.Bl, 27)
val Bl28 = PunctCoordinate(Meridian.Bl, 28)

sealed class YuRelevant(
        val labelShort: String,
        val labelLong: String,
        val yuPunct: PunctCoordinate,
        val localisation: String
) {

    @Suppress("LeakingThis")
    val isExtra = this is YuRelevantExtraMeridian

    companion object {
        private val VALUES: List<YuRelevant> = listOf(
                YuRelevantMeridian(Meridian.Lu, Bl13, "3. BW"),
                YuRelevantMeridian(Meridian.Pk, Bl14, "4. BW"),
                YuRelevantMeridian(Meridian.He, Bl15, "5. BW"),
                YuRelevantExtraMeridian("Gv", "Gouverneur", Bl16, "6. BW"),
                YuRelevantExtraMeridian("Zf", "Zwerchfell", Bl17, "7. BW"),
                YuRelevantMeridian(Meridian.Le, Bl18, "9. BW"),
                YuRelevantMeridian(Meridian.Gb, Bl19, "10. BW"),
                YuRelevantMeridian(Meridian.MP, Bl20, "11. BW"),
                YuRelevantMeridian(Meridian.Ma, Bl21, "12. BW"),
                YuRelevantMeridian(Meridian.EEE, Bl22, "1. LW"),
                YuRelevantMeridian(Meridian.Ni, Bl23, "2. LW"),
                YuRelevantExtraMeridian("QiMeer", "Meer der Lebensenergie", Bl24, "3. LW"),
                YuRelevantMeridian(Meridian.Di, Bl25, "4. LW"),
                YuRelevantExtraMeridian("QiUrspr", "Ursprungsenergie", Bl26, "5. LW"),
                YuRelevantMeridian(Meridian.Due, Bl27, "1. KBL"),
                YuRelevantMeridian(Meridian.Bl, Bl28, "2. KBL")
        )
        private val VALUES_ARRAY = VALUES.toTypedArray()

        fun values() = VALUES
        fun valuesArray() = VALUES_ARRAY

        // nice search logic ;)
//        private val yuRelevantByMeridian by lazy { VALUES.filter { it is YuRelevantMeridian }.associateBy { (it as YuRelevantMeridian).meridian } }
//        private fun yuRelevantByMeridian(search: Meridian): YuRelevant = yuRelevantByMeridian[search]!!
    }

    class YuRelevantMeridian(val meridian: Meridian, yuPunct: PunctCoordinate, localisation: String)
        : YuRelevant(meridian.nameShort, meridian.nameLong, yuPunct, localisation) {

    }

    class YuRelevantExtraMeridian(labelShort: String, labelLong: String, yuPunct: PunctCoordinate, localisation: String)
        : YuRelevant(labelShort, labelLong, yuPunct, localisation) {

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
            val meridian = Meridian.values().firstOrNull { string.startsWith(it.nameShort) } ?: throw IllegalArgumentException("Invalid meridian name: [$string]")
            val number = string.substring(meridian.nameShort.length).toInt()
            if (number < 1 || number > meridian.points) throw IllegalArgumentException("Invalid point number: $number for meridian $meridian")
            return PunctCoordinate(meridian, number)
        }
    }

    /** Meridian short name + point number; e.g.: "Lu1" */
    val label = meridian.nameShort + point
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
    /** Lu, Di */
    val nameShort: String
    /** Lunge, Dickdarm */
    val nameLong: String
    /** count of tsubos */
    val points: Int
}

enum class Meridian(
        override val nameShort: String,
        override val nameLong: String,
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

    val prettyFormatted: String by lazy {
        (if (isRight) "(R) " else "") + text
    }
}

data class Question(
        val id: String,
        val text: String,
        val answers: List<Answer>
) {
    companion object // for (test) extensions

    val rightAnswers = answers.filter { it.isRight }
    val indicesOfRightAnswers = rightAnswers.mapIndexed { i, answer -> i }

    val prettyFormatted: String by lazy {
        "[$id] $text\n" + answers.map { "\t- " + it.prettyFormatted }.joinToString("\n")
    }

}
fun List<Question>.prettyFormatQuestions() = map { it.prettyFormatted }.joinToString("\n\n")
fun List<Question>.prettyPrintQuestions() { println(prettyFormatQuestions())}
