package org.butterbrot.heve.ubill.view

import android.widget.TextView
import org.butterbrot.heve.ubill.InterfaceConstants


interface NumberAware<T> {

    var dynamicColoringEnabled: Boolean

    fun getText(): T

    fun getNumber(): Int {
        val text = getText().toString()
        if (text.isEmpty()) {
            return 0
        }
        return (text.toDouble() * 100).toInt()
    }

    fun getBufferType(): TextView.BufferType

    fun setText(text: CharSequence?, type: TextView.BufferType?)

    fun setTextColor(color: Int)

    fun setNumber(number: Int) {
        if (dynamicColoringEnabled) {
            adjustColor(number)
        }

        setText((number.toDouble()/100).toString(), getBufferType())
    }

    fun adjustColor(number: Int) {
        val color: Int = when {
            number == 0 -> InterfaceConstants.COLOR_AMOUNT_ZERO
            number < 0 -> InterfaceConstants.COLOR_AMOUNT_NEGATIV
            else -> InterfaceConstants.COLOR_AMOUNT_POSITIV
        }
        setTextColor(color)
    }

}