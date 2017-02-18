package yobu.christophpickl.github.com.yobu.logic

import android.support.annotation.VisibleForTesting
import java.util.*


fun <T> List<T>.randomizeElements(): List<T> {
    return toMutableList().apply {
        Collections.shuffle(this)
    }
}


fun <E> List<E>.randomElementsExcept(randomElementsCount: Int, except: E, rand: RandX = RandXImpl): List<E> {
    return rand.randomElementsExcept(this, randomElementsCount, except)
}


fun <T> List<T>.randomElement(): T {
    return RandXImpl.randomOf(this)
}

fun <T> Set<T>.randomElement(): T {
    return RandXImpl.randomOf(this.toList())
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
    override fun <T> randomElementsExcept(items: List<T>, randomElementsCount: Int, except: T): List<T> {
        // TODO check randomElementsCount for limits
        val result = mutableListOf<T>()
        var honeypot = items.toMutableList().minus(except)
        while (result.size != randomElementsCount) {
            val randomElement = randomOf(honeypot)
            result += randomElement
            honeypot = honeypot.minus(randomElement)
        }
        return result
    }

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

    override fun <T> randomOf(items: Array<T>, except: T): T {
        if (items.size <= 1) throw IllegalArgumentException("array must contain at least 2 elements: $items")
        var randItem: T?
        do {
            randItem = items[randomBetween(0, items.size - 1)]
        } while (randItem == except)
        return randItem!!
    }

    override fun <T> randomOf(items: List<T>) = items[randomBetween(0, items.size - 1)]
    override fun <T> randomOf(items: List<T>, except: T): T {
        // TODO copy'n'paste from array
        if (items.size <= 1) throw IllegalArgumentException("list must contain at least 2 elements: $items")
        var randItem: T?
        do {
            randItem = items[randomBetween(0, items.size - 1)]
        } while (randItem == except)
        return randItem!!
    }


    private fun <T> Distribution<T>.sumOfPercents() = this.items.sumBy { it.percent }
}

interface RandX {
    fun <T> distributed(distribution: Distribution<T>): T
    fun randomBetween(from: Int, to: Int, except: Int? = null): Int

    fun <T> randomOf(items: Array<T>, except: T): T
    fun <T> randomOf(items: List<T>): T
    fun <T> randomOf(items: List<T>, except: T): T
    fun <T> randomElementsExcept(items: List<T>, randomElementsCount: Int, except: T): List<T>
}

private fun rand(diff: Int) = Math.round(Math.random() * diff).toInt()
