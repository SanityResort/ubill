package org.butterbrot.heve.ubill

import android.os.Bundle
import android.support.design.widget.Snackbar
import kotlinx.android.synthetic.main.activity_bill_list.*
import kotlinx.android.synthetic.main.activity_create_bill.*
import org.butterbrot.heve.ubill.entity.Bill


class CreateBillActivity : BoxActivity<Bill>(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_bill)
        setSupportActionBar(toolbar)

        new_fellow.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        
        add_fellow.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
}