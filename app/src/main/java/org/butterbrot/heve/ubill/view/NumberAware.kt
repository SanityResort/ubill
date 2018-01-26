package org.butterbrot.heve.ubill.view

import android.widget.TextView
import org.butterbrot.heve.ubill.InterfaceConstants
import java.text.NumberFormat


interface NumberAware<T> {

    var dynamicColoringEnabled: Boolean

    var numberFormat: NumberFormat

    fun getText(): T

    fun getNumber(): Int {
        val text = getText().toString()
        if (text.isEmpty()) {
            return 0
        }
        return (numberFormat.parse(text).toDouble() * 100).toInt()
    }

    fun getBufferType(): TextView.BufferType

    fun setText(text: CharSequence?, type: TextView.BufferType?)

    fun setTextColor(color: Int)

    fun setNumber(number: Int) {
        adjustColor(number)
        setText(numberFormat.format(number.toDouble()/100), getBufferType())
    }

    fun adjustColor(number: Int) {
        if (dynamicColoringEnabled) {
            val color: Int = when {
                number == 0 -> InterfaceConstants.COLOR_AMOUNT_ZERO
                number < 0 -> InterfaceConstants.COLOR_AMOUNT_NEGATIV
                else -> InterfaceConstants.COLOR_AMOUNT_POSITIV
            }
            setTextColor(color)
        }
    }
}