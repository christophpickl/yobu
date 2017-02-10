package yobu.christophpickl.github.com.yobu.testinfra


fun doCoupleOfTimes(code: () -> Unit) {
    1.rangeTo(100).forEach { code() }
}
