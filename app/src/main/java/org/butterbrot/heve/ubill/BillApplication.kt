package org.butterbrot.heve.ubill;

import android.app.Application
import io.objectbox.BoxStore
import org.butterbrot.heve.ubill.entity.MyObjectBox


class BillApplication: Application() {

    lateinit var boxStore: BoxStore

    override fun onCreate() {
        super.onCreate()
        boxStore = MyObjectBox.builder().androidContext(this).build()
    }

    override fun onTerminate() {
        super.onTerminate()
        boxStore.closeThreadResources()
    }
}