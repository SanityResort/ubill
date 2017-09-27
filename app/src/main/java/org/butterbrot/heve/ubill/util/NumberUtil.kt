package org.butterbrot.heve.ubill.util

import android.content.Context


object NumberUtil {

    fun getDimension(resId: Int, context: Context): Int {
        val scale = context.resources.displayMetrics.density
        val dps = context.resources.getDimension(resId)
        return (dps * scale + 0.5f).toInt()
    }

}