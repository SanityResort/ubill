package org.butterbrot.heve.ubill.view

import android.text.Editable


interface NumberGetter : NumberSetter {

    fun getText(): Editable

    fun getNumber(): Int {
        return (getText().toString().toDouble() * 100).toInt()
    }
}