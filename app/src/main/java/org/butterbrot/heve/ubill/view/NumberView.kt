package org.butterbrot.heve.ubill.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.TextView
import org.butterbrot.heve.ubill.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*


class NumberView @JvmOverloads constructor (
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.textViewStyle,
        defStyleRes: Int = 0): TextView(context, attributeSet, defStyleAttr, defStyleRes), NumberAware<CharSequence> {

    override var dynamicColoringEnabled: Boolean = true

    override var numberFormat: NumberFormat = DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.US))

    init {
        val typedArray:TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.NumberView, 0, 0)

        dynamicColoringEnabled = typedArray.getBoolean(R.styleable.NumberView_dynamicTextColoring, true)

        typedArray.recycle()
    }

    override fun getBufferType(): BufferType {
        return BufferType.NORMAL
    }
}