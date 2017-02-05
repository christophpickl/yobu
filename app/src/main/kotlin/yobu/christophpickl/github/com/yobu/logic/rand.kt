package yobu.christophpickl.github.com.yobu.logic

import java.util.*


fun <T> List<T>.randomizeElements(): List<T> {
    return toMutableList().apply {
        Collections.shuffle(this)
    }
}

fun <T> List<T>.randomElement(): T {
    return Random.randomOf(this)
}


object Random {
    fun <T> randomOf(array: Array<T>, except: T): T {
        if (array.size <= 1) throw IllegalArgumentException("array must contain at least 2 elements: $array")
        var randItem: T?
        do {
            randItem = array[randomBetween(0, array.size - 1)]
        } while (randItem == except)
        return randItem!!
    }

    fun <T> randomOf(list: List<T>) = list[randomBetween(0, list.size - 1)]

    fun randomBetween(from: Int, to: Int, except: Int? = null): Int {
        val diff = to - from
        if (except == null) {
            return rand(diff) + from
        }
        if (diff <= 1) throw IllegalArgumentException("difference between from and to must be at least 2 big: from=$from, to=$to")
        var randPos: Int?
        do {
            randPos = rand(diff) + from
        } while (randPos == except)
        return randPos!!
    }

    private fun rand(diff: Int) = Math.round(Math.random() * diff).toInt()
}
