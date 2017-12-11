package org.butterbrot.heve.ubill;

import android.app.Application
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import org.butterbrot.heve.ubill.entity.MyObjectBox


class BillApplication: Application() {

    lateinit var boxStore: BoxStore

    override fun onCreate() {
        super.onCreate()
        boxStore = MyObjectBox.builder().androidContext(this).build()
        AndroidObjectBrowser(boxStore).start(this);
    }

    override fun onTerminate() {
        super.onTerminate()
        boxStore.closeThreadResources()
    }
}