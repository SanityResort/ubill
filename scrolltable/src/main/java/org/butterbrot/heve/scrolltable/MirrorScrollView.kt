package org.butterbrot.heve.scrolltable

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.ScrollView

class MirrorScrollView @JvmOverloads
    constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
        ScrollView(context, attributeSet, defStyleAttr, defStyleRes) {

    lateinit var mirror: ScrollView
    private val mirrorId: Int

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MirrorScrollView, 0, 0)

        mirrorId = typedArray.getResourceId(R.styleable.MirrorScrollView_vmirror, -1)

        typedArray.recycle()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        mirror.scrollTo(0, t)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mirrorId != -1) {
            mirror = rootView.findViewById(mirrorId)
        }
    }


}