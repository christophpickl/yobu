package yobu.christophpickl.github.com.yobu.logic

import yobu.christophpickl.github.com.yobu.Answer
import yobu.christophpickl.github.com.yobu.Question


object StaticQuestions {

    val questions = listOf(

            //region BO questions

            Question(// 11. rippe => MP„ (Le13), 12. rippe => Ni (Gb25)
                    id = "BoRippen",
                    title = "Bo Punkt Rippenenden",
                    text = "Welche zwei Bo Punkte liegen auf den Rippenenden?",
                    answers = listOf(
                            Answer("MP, Ni", isRight = true),
                            Answer("MP, KG"),
                            Answer("Gb, Le"),
                            Answer("Ni, Bl")
                    )
            ),
            Question(//  6. ICR => Le14, 7. ICR => Gb24
                    id = "BoICR",
                    title = "Bo Punkt ICRs",
                    text = "Welche zwei Bo Punkte liegen in den ICRs?",
                    answers = listOf(
                            Answer("Le, Gb", isRight = true),
                            Answer("MP, Ni"),
                            Answer("Di, Le"),
                            Answer("Mi, Gb")
                    )
            ),
            Question(// wer liegt auf seinem eigenen meridian? => Lu(1), Gb(24), Le(14)
                    id = "BoOwnMerid",
                    title = "Bo Punkt eigener Meridian",
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
            ),
            Question(// wer liegt nicht auf KG? => Lu, Di, MP, Ni, Gb, Le
                    id = "BoNotKG",
                    title = "Bo Punkt nicht KG",
                    text = "Welcher Bo Punkt liegt nicht am KG?",
                    answers = listOf(
                            // or maybe reduce to one single right answer containing "Di, Ni, Le"?
                            Answer("Di", isRight = true),
                            Answer("Ni", isRight = true),
                            Answer("Le", isRight = true),
                            // ... Lu, MP, Gb as well
                            Answer("Ma"),
                            Answer("Pk"),
                            Answer("3E")
                    )
            ),
            Question(
                    id = "BoNipple",
                    title = "Bo Punkt Brustwarzen",
                    text = "Welcher Bo Punkt liegt zwischen den Brustwarzen?",
                    answers = listOf(
                            Answer("Pk", isRight = true), // KG17
                            Answer("He"),
                            Answer("Lu"),
                            Answer("MP"),
                            Answer("Gb")
                    )
            ),
            Question(
                    id = "BoBellyButtonSide",
                    title = "Bo Punkt Nabel seitlich",
                    text = "Welcher Bo Punkt liegt als einziger seitlich vom Nabel?",
                    answers = listOf(
                            Answer("Di", isRight = true), // Ma25
                            Answer("He"),
                            Answer("Lu"),
                            Answer("MP"),
                            Answer("Gb")
                    )
            )

            //endregion

            //region YU questions

//            Question(
//                    id = "YuXXX",
//                    title = "",
//                    text = "",
//                    answers = listOf(
//                            Answer("1", isRight = true),
//                            Answer("2")
//                    )
//            )

            //endregion

    )

}
