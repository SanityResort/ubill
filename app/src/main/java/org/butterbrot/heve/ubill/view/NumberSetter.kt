package org.butterbrot.heve.ubill.view

import android.widget.TextView


interface NumberSetter {

    fun getBufferType():TextView.BufferType

    fun setText(text: CharSequence?, type: TextView.BufferType?)

    fun setNumber(number: Int) {
        setText((number.toDouble()/100).toString(), getBufferType())
    }

}