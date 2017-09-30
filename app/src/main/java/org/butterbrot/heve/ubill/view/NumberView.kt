package org.butterbrot.heve.ubill.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.TextView
import org.butterbrot.heve.ubill.R


class NumberView(
        context: Context,
        attributeSet: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int): TextView(context, attributeSet, defStyleAttr, defStyleRes), NumberSetter {

    private var dynamicColoringEnabled: Boolean = true

    constructor(context: Context) : this(context, null, android.R.attr.textViewStyle, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, android.R.attr.textViewStyle, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int)
            : this(context, attributeSet, defStyleAttr, 0)

    init {
        val typedArray:TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.NumberView, 0, 0)

        dynamicColoringEnabled = typedArray.getBoolean(R.styleable.NumberView_dynamicTextColoring, true)

        typedArray.recycle()
    }

    override fun getBufferType(): BufferType {
        return BufferType.NORMAL
    }

    override fun setNumber(number: Int) {
        if (dynamicColoringEnabled) {
            adjustColor(number)
        }

        super.setNumber(number)
    }

}