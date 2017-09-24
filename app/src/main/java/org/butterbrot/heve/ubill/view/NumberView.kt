package org.butterbrot.heve.ubill.view

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.widget.TextView


class NumberView(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
    : TextView(context, attributeSet, defStyleAttr, defStyleRes), NumberSetter {

    override fun getBufferType(): BufferType {
        return BufferType.NORMAL
    }

    constructor(context: Context) : this(context, null, 0, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int)
            : this(context, attributeSet, defStyleAttr, 0)

}