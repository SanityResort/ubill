package org.butterbrot.heve.ubill.view

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText


class EditNumber(
        context: Context,
        attributeSet: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int): EditText(context, attributeSet, defStyleAttr, defStyleRes), NumberGetter<Editable> {

    override fun getBufferType(): BufferType {
        return BufferType.EDITABLE
    }

    constructor(context: Context) : this(context, null, android.R.attr.editTextStyle, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, android.R.attr.editTextStyle, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int)
            : this(context, attributeSet, defStyleAttr, 0)

    init {

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                 adjustColor(getNumber())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // NOOP
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // NOOP
            }

        })
        inputType = InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL
    }
}