package org.butterbrot.heve.ubill.view

import android.text.Editable


interface NumberGetter : NumberSetter {

    fun getText(): Editable

    fun getNumber(): Int {
        val text = getText().toString()
        if (text.isEmpty()) {
            return 0
        }
        return (text.toDouble() * 100).toInt()
    }
}