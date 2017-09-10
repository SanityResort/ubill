package org.butterbrot.heve.ubill;

import android.app.Application;

import org.butterbrot.heve.ubill.entity.MyObjectBox;

import io.objectbox.BoxStore;


class BillApplication: Application() {

    lateinit var boxStore: BoxStore

    override fun onCreate() {
        super.onCreate()
        boxStore = MyObjectBox.builder().androidContext(this).build()
    }
}