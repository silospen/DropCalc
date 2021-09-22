package com.silospen.dropcalc

import org.apache.commons.math3.util.Precision
import kotlin.math.pow

class Probability(private val num: Double) {

    constructor(numerator: Int, denominator: Int) : this(numerator / denominator.toDouble())
    constructor(numerator: Long, denominator: Long) : this(numerator / denominator.toDouble())

    fun subtract(other: Probability) = Probability(num - other.num)
    fun add(other: Probability) = Probability(num + other.num)
    fun multiply(other: Probability) = Probability(num * other.num)
    fun pow(power: Int) = Probability(num.pow(power))
    fun toDouble() = num
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Probability

        if (!Precision.equals(num, other.num, 0.00000000001)) return false

        return true
    }

    override fun hashCode(): Int {
        return num.hashCode()
    }

    override fun toString(): String {
        return "ProbNum(num=$num)"
    }

    companion object {
        val ONE = Probability(1.0)
        val ZERO = Probability(0.0)
    }
}