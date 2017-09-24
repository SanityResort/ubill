package org.butterbrot.heve.ubill.view

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.widget.EditText


class EditNumber(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
    : EditText(context, attributeSet, defStyleAttr, defStyleRes) {

    constructor(context: Context) : this(context, null, 0, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int)
            : this(context, attributeSet, defStyleAttr, 0)

    init {
        inputType = InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL
    }
}