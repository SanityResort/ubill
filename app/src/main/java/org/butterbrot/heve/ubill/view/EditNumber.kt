package org.butterbrot.heve.ubill.view

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.widget.EditText


class EditNumber(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
    : EditText(context, attributeSet, defStyleAttr, defStyleRes), NumberGetter {
    override fun getBufferType(): BufferType {
        return BufferType.EDITABLE
    }

    constructor(context: Context) : this(context, null, android.R.attr.editTextStyle, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, android.R.attr.editTextStyle, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int)
            : this(context, attributeSet, defStyleAttr, 0)

    init {
        inputType = InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL
    }
}