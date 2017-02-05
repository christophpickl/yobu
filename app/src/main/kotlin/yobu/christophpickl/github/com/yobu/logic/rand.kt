package yobu.christophpickl.github.com.yobu.logic

import android.support.annotation.VisibleForTesting
import java.util.*


fun <T> List<T>.randomizeElements(): List<T> {
    return toMutableList().apply {
        Collections.shuffle(this)
    }
}

fun <T> List<T>.randomElement(): T {
    return RandXImpl.randomOf(this)
}

data class DistributionItem<T>(val percent: Int, val value: T)
data class Distribution<T>(val items: List<DistributionItem<T>>) {
    init {
        if (items.isEmpty()) throw IllegalArgumentException("Distribution must not be empty!")
    }
}
fun <T> distributionOf(vararg pairs: Pair<Int, T>) =
        Distribution<T>(pairs.map { DistributionItem(it.first, it.second) })

object RandXImpl : RandX {

    override fun <T> distributed(distribution: Distribution<T>): T {
        val rand = randomBetween(0, 100)
        return _distributed(distribution, rand)
    }

    @VisibleForTesting fun <T> _distributed(distribution: Distribution<T>, rand: Int): T {
        if (distribution.sumOfPercents() != 100)
            throw IllegalArgumentException("Distribution percent must be sum of 100: $distribution")

        var currentPercent = 0
        for (item in distribution.items) {
            currentPercent += item.percent
            if (rand <= currentPercent) {
                return item.value
            }
        }
        throw IllegalStateException("Distribution algorithm failed! rand=$rand, distribution=$distribution (currentPercent=$currentPercent)")
    }


    override fun randomBetween(from: Int, to: Int, except: Int?): Int {
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

    override fun <T> randomOf(array: Array<T>, except: T): T {
        if (array.size <= 1) throw IllegalArgumentException("array must contain at least 2 elements: $array")
        var randItem: T?
        do {
            randItem = array[randomBetween(0, array.size - 1)]
        } while (randItem == except)
        return randItem!!
    }

    override fun <T> randomOf(list: List<T>) = list[randomBetween(0, list.size - 1)]

    private fun <T> Distribution<T>.sumOfPercents() = this.items.sumBy { it.percent }
}

interface RandX {
    fun <T> distributed(distribution: Distribution<T>): T
    fun randomBetween(from: Int, to: Int, except: Int? = null): Int

    fun <T> randomOf(array: Array<T>, except: T): T
    fun <T> randomOf(list: List<T>): T
}

private fun rand(diff: Int) = Math.round(Math.random() * diff).toInt()
