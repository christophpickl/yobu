package yobu_main

import yobu.christophpickl.github.com.yobu.JsonQuestionReader
import java.io.File
import java.io.FileInputStream

// android kotlin behaves strange for main methods declared in src/main/kotlin ...
fun main(args: Array<String>) {
    JsonQuestionReader().read(FileInputStream(File("app/src/main/res/raw/questions_catalog_catalog.json")))
}