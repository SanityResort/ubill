package org.butterbrot.heve.scrolltable

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.HorizontalScrollView


class MirrorHScrollView @JvmOverloads
    constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
        HorizontalScrollView(context, attributeSet, defStyleAttr, defStyleRes) {

    lateinit var mirror: HorizontalScrollView
    private val mirrorId: Int

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MirrorHScrollView, 0, 0)

        mirrorId = typedArray.getResourceId(R.styleable.MirrorHScrollView_hmirror, -1)

        typedArray.recycle()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        mirror.scrollTo(l, 0)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mirrorId != -1) {
            mirror = rootView.findViewById(mirrorId)
        }
    }
}