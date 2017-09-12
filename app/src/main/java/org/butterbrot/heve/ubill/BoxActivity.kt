package org.butterbrot.heve.ubill

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.objectbox.Box
import java.lang.reflect.ParameterizedType


abstract class BoxActivity<T> : AppCompatActivity() {

    lateinit var box: Box<T>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        box = (application as BillApplication).boxStore.boxFor(getBoxType())

    }

    protected fun getBoxType(): Class<T> {
        return (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>
    }

}