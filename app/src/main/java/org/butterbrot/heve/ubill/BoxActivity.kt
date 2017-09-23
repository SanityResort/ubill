package org.butterbrot.heve.ubill

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import io.objectbox.Box
import kotlinx.android.synthetic.main.activity_bill_list.*
import java.lang.reflect.ParameterizedType


abstract class BoxActivity<T> : AppCompatActivity() {

    lateinit var box: Box<T>

    protected abstract val menuId: Int
    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        setSupportActionBar(toolbar)
        box = (application as BillApplication).boxStore.boxFor(getBoxType())

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds itemsParam to the action bar if it is present.
        menuInflater.inflate(menuId, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        box.closeThreadResources()
    }

    protected fun getBoxType(): Class<T> {
        return (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>
    }

}