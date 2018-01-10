package org.butterbrot.heve.scrolltable

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ScrollTableFragment : Fragment() {
    private lateinit var scrollTable: ScrollTable
    private var typedArray: TypedArray? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        scrollTable = inflater?.inflate(R.layout.scrolltable, container, false) as ScrollTable
        if (typedArray != null) {
            scrollTable.properties = ScrollTable.Properties(typedArray!!)
            typedArray?.recycle()
        }
        return scrollTable
    }

    override fun onInflate(context: Context?, attrs: AttributeSet?, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        if (context != null) {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScrollTable, 0, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        scrollTable.removeAllViews()
        scrollTable.populate()
    }
}