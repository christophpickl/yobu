package yobu.christophpickl.github.com.yobu.logic

import org.json.JSONArray
import org.json.JSONObject
import yobu.christophpickl.github.com.yobu.*
import yobu.christophpickl.github.com.yobu.common.readString
import java.io.InputStream


class QuestionsLoader {

//    * wer liegt nicht auf KG? => Lu, Di, Mi, Ni, Gb, Le
//    * wer liegt zwischen brustwarzen? => Pk (KG17)
//    * wer liegt (als einziger) (2C) seitlich vom nabel? => Di (Ma25)
    fun load(): List<Question> {
        return listOf(
                Question( // wer auf der 11./12. rippe? => Mi (Le13), Ni (Gb25)
                        id = "BoRippen",
                        text = "Welche zwei Bo Punkte liegen auf den Rippenenden?",
                        answers = listOf(
                                Answer("MP, Ni", isRight = true),
                                Answer("MP, KG"),
                                Answer("Gb, Le"),
                                Answer("Ni, Bl")
                        )
                ),
                Question( // wer in 6./7. ICR? => Le (Le14, 6.), Gb (Gb24, 7.)
                        id = "BoICR",
                        text = "Welche zwei Bo Punkte liegen in den ICRs?",
                        answers = listOf(
                                Answer("Le, Gb", isRight = true),
                                Answer("MP, Ni"),
                                Answer("Di, Le"),
                                Answer("Mi, Gb")
                        )
                ),
                Question( // wer liegt auf seinem eigenen meridian? => Lu(1), Gb(24), Le(14)
                        id = "BoOwnMerid",
                        text = "Welcher Bo Punkt liegt auf seinem eigenen Meridian?",
                        answers = listOf(
                                // or maybe reduce to one single right answer containing "Lu, Gb, Le"?
                                Answer("Lu", isRight = true),
                                Answer("Gb", isRight = true),
                                Answer("Le", isRight = true),
                                Answer("MP"),
                                Answer("Ma"),
                                Answer("Bl"),
                                Answer("Ni")
                        )
                )
        )
    }

}
