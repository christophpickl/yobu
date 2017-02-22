package yobu.christophpickl.github.com.yobu.common


fun <T> List<T>.prettyPrint() {
    println(map { "- " + it.toString() }.joinToString("\n"))
}
