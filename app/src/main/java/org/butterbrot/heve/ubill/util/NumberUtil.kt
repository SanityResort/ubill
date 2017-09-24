package org.butterbrot.heve.ubill.util

import android.content.Context


object NumberUtil {

    fun toText(number: Int): String {
        return (number.toDouble()/100).toString()
    }

    fun toInt(text: String): Int {
        return (text.toDouble()*100).toInt()
    }

    fun getDimension(resId: Int, context: Context): Int {
        val scale = context.resources.displayMetrics.density
        val dps = context.resources.getDimension(resId)
        return (dps * scale + 0.5f).toInt()
    }

}