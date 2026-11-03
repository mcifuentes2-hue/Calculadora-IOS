package com.utp.calculadora

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.Locale
import kotlin.math.abs

private const val ADD = "+"
private const val SUBTRACTION = "−"
private const val MULTIPLY = "×"
private const val DIVIDE = "÷"

private const val MAX_DIGITS = 12

data class HistoryItem(val expression: String, val result: String)

class CalculatorViewModel : ViewModel() {

    private val model = CalculatorModel()

    val result = mutableStateOf("0")
    val hasError = mutableStateOf(false)
    val history = mutableStateListOf<HistoryItem>()

    var isTyping by mutableStateOf(false)
        private set
    var operator by mutableStateOf<String?>(null)
        private set

    private var firstNumber: Double? = null
    private var lastOperator: String? = null
    private var lastNumber: Double? = null

    val clearLabel: String
        get() = if (isTyping && result.value != "0") "C" else "AC"

    val displayText: String
        get() = groupDigits(result.value)

    fun isOperatorActive(symbol: String): Boolean = operator == symbol && !isTyping

    fun manageClickButtons(valueButton: String) {
        if (valueButton.isEmpty()) return
        if (hasError.value && valueButton != "AC") return

        when (valueButton) {
            "AC" -> clearAll()
            "C" -> clearEntry()
            "≪" -> deleteLast()
            "%" -> percentage()
            "+/−" -> changeSign()
            "," -> addDecimal()
            "=" -> calculateResult()
            ADD, SUBTRACTION, MULTIPLY, DIVIDE -> selectOperator(valueButton)
            else -> if (valueButton[0].isDigit()) addDigit(valueButton)
        }
    }

    private fun addDigit(digit: String) {
        val current = result.value
        result.value = when {
            !isTyping || current == "0" -> digit
            current.count { it.isDigit() } >= MAX_DIGITS -> current
            else -> current + digit
        }
        isTyping = true
    }

    private fun addDecimal() {
        result.value = when {
            !isTyping -> "0,"
            result.value.contains(",") -> result.value
            else -> result.value + ","
        }
        isTyping = true
    }

    private fun deleteLast() {
        if (!isTyping) return
        val text = result.value.dropLast(1)
        result.value = if (text.isEmpty() || text == "-") "0" else text
    }

    private fun changeSign() {
        val current = result.value
        result.value = when {
            current == "0" -> current
            current.startsWith("-") -> current.removePrefix("-")
            else -> "-$current"
        }
    }

    private fun percentage() {
        val value = readResult() ?: return
        val base = firstNumber
        val pending = operator
        val percent = if (base != null && (pending == ADD || pending == SUBTRACTION)) {
            base * value / 100
        } else {
            value / 100
        }
        result.value = format(percent)
        isTyping = false
    }

    private fun selectOperator(newOperator: String) {
        val current = readResult() ?: return
        val pending = operator

        if (pending != null && isTyping) {
            val left = firstNumber ?: current
            val value = operate(left, current, pending) ?: return
            addToHistory(left, pending, current, value)
            firstNumber = value
            result.value = format(value)
        } else if (pending == null) {
            firstNumber = current
        }

        operator = newOperator
        lastOperator = null
        lastNumber = null
        isTyping = false
    }

    private fun calculateResult() {
        val current = readResult() ?: return
        val pending = operator

        val left: Double
        val right: Double
        val op: String
        if (pending != null) {
            op = pending
            left = firstNumber ?: current
            right = current
        } else {
            op = lastOperator ?: return
            left = current
            right = lastNumber ?: return
        }

        val value = operate(left, right, op) ?: return
        addToHistory(left, op, right, value)

        lastOperator = op
        lastNumber = right
        operator = null
        firstNumber = null
        isTyping = false
        result.value = format(value)
    }

    private fun clearEntry() {
        result.value = "0"
        isTyping = false
    }

    private fun clearAll() {
        result.value = "0"
        hasError.value = false
        firstNumber = null
        operator = null
        lastOperator = null
        lastNumber = null
        isTyping = false
    }

    fun clearHistory() {
        history.clear()
    }

    private fun operate(left: Double, right: Double, op: String): Double? {
        if (op == DIVIDE && right == 0.0) {
            showError()
            return null
        }
        val value = when (op) {
            ADD -> model.add(left, right)
            SUBTRACTION -> model.subtraction(left, right)
            MULTIPLY -> model.multiply(left, right)
            else -> model.divide(left, right)
        }
        if (value.isNaN() || value.isInfinite()) {
            showError()
            return null
        }
        return value
    }

    private fun showError() {
        clearAll()
        hasError.value = true
    }

    private fun addToHistory(left: Double, op: String, right: Double, value: Double) {
        history.add(
            0,
            HistoryItem(
                expression = "${groupDigits(format(left))} $op ${groupDigits(format(right))}",
                result = groupDigits(format(value))
            )
        )
    }

    private fun readResult(): Double? {
        return result.value.replace(",", ".").toDoubleOrNull()
    }

    private fun format(value: Double): String {
        if (value == 0.0) return "0"
        val text = if (abs(value) >= 1.0E12 || abs(value) < 1.0E-8) {
            String.format(Locale.US, "%.6E", value)
        } else {
            String.format(Locale.US, "%.8f", value).trimEnd('0').trimEnd('.')
        }
        return text.replace(".", ",")
    }

}

fun groupDigits(text: String): String {
    if (text.contains("E")) return text
    val negative = text.startsWith("-")
    val body = text.removePrefix("-")
    val integerPart = body.substringBefore(",")
    val decimalPart = if (body.contains(",")) "," + body.substringAfter(",") else ""
    val grouped = integerPart.reversed().chunked(3).joinToString(".").reversed()
    return (if (negative) "-" else "") + grouped + decimalPart
}
