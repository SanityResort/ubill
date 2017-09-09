package org.butterbrot.heve.ubill;

import android.app.Application;
import org.butterbrot.heve.ubill.entity.MyObjectBox;

import io.objectbox.BoxStore;


public class BillApplication extends Application {

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        boxStore = MyObjectBox.builder().androidContext(this).build();

    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}