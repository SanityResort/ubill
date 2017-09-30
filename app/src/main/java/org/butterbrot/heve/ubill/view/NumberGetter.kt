package org.butterbrot.heve.ubill.view

import android.text.Editable


interface NumberGetter<T> : NumberSetter {

    fun getText(): T

    fun getNumber(): Int {
        val text = getText().toString()
        if (text.isEmpty()) {
            return 0
        }
        return (text.toDouble() * 100).toInt()
    }
}