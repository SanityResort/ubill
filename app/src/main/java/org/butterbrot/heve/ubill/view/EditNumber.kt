package org.butterbrot.heve.ubill.view

import android.content.Context
import android.content.res.TypedArray
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import org.butterbrot.heve.ubill.R
import java.text.DecimalFormat
import java.text.NumberFormat


class EditNumber @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.editTextStyle,
        defStyleRes: Int = 0): EditText(context, attributeSet, defStyleAttr, defStyleRes), NumberAware<Editable> {

    override var dynamicColoringEnabled: Boolean = true

    override var numberFormat: NumberFormat = DecimalFormat("#0.00")

    override fun getBufferType(): BufferType {
        return BufferType.EDITABLE
    }

    init {

        val typedArray: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.NumberView, 0, 0)

        dynamicColoringEnabled = typedArray.getBoolean(R.styleable.NumberView_dynamicTextColoring, true)

        typedArray.recycle()

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