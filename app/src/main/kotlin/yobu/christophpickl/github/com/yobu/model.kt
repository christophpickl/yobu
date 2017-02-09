package yobu.christophpickl.github.com.yobu

val Lu1 = PunctCoordinate(Meridian.Lu, 1)
val Ma25 = PunctCoordinate(Meridian.Ma, 25)
val Le13 = PunctCoordinate(Meridian.Le, 13)
val Le14 = PunctCoordinate(Meridian.Le, 14)
val Gb24= PunctCoordinate(Meridian.Gb, 24)
val Gb25= PunctCoordinate(Meridian.Gb, 25)

val KG3 = PunctCoordinate(Meridian.KG, 3)
val KG4 = PunctCoordinate(Meridian.KG, 4)
val KG5 = PunctCoordinate(Meridian.KG, 5)
val KG7 = PunctCoordinate(Meridian.KG, 7)
val KG12 = PunctCoordinate(Meridian.KG, 12)
val KG14 = PunctCoordinate(Meridian.KG, 14)
val KG17 = PunctCoordinate(Meridian.KG, 17)

enum class BoRelevantMeridian(
        val meridian: MainMeridian,
        val boPunct: PunctCoordinate,
        val localisation: String
) : IMeridian by meridian {
    Lu(MainMeridian.Lu, Lu1, "Schlüsselbein"),
    Di(MainMeridian.Di, Ma25, "2C lat Nabel"),
    Ma(MainMeridian.Ma, KG12, "4C cranial Nabel"),
    MP(MainMeridian.MP, Le13, "11. Rippe"),
    He(MainMeridian.He, KG14, "6C cranial Nabel"),
    Due(MainMeridian.Due, KG4, "3C caudal Nabel"),
    Bl(MainMeridian.Bl, KG3, "4C caudal Nabel"),
    Ni(MainMeridian.Ni, Gb25, "12. Rippe"),
    Pk(MainMeridian.Pk, KG17, "Zw. Brustwarzen"),
    EEE(MainMeridian.EEE, KG7, "1C caudal Nabel"),
    Gb(MainMeridian.Gb, Gb24, "7. ICR"),
    Le(MainMeridian.Le, Le14, "6. ICR");
}

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
    Le(Meridian.Le)
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
    val rightAnswer = answers.first { it.isRight }
    val indexOfRightAnswer = answers.indexOfFirst { it.isRight }

    companion object // for (test) extensions
}
