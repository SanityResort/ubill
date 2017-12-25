package org.butterbrot.heve.ubill.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.TextView
import org.butterbrot.heve.ubill.R


class NumberView @JvmOverloads constructor (
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0): TextView(context, attributeSet, defStyleAttr, defStyleRes), NumberAware<CharSequence> {

    override var dynamicColoringEnabled: Boolean = true

    init {
        val typedArray:TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.NumberView, 0, 0)

        dynamicColoringEnabled = typedArray.getBoolean(R.styleable.NumberView_dynamicTextColoring, true)

        typedArray.recycle()
    }

    override fun getBufferType(): BufferType {
        return BufferType.NORMAL
    }
}