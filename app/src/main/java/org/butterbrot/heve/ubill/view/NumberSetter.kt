package org.butterbrot.heve.ubill.view

import android.widget.TextView
import org.butterbrot.heve.ubill.InterfaceConstants


interface NumberSetter {

    fun getBufferType():TextView.BufferType

    fun setText(text: CharSequence?, type: TextView.BufferType?)

    fun setTextColor(color: Int)

    fun setNumber(number: Int) {
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