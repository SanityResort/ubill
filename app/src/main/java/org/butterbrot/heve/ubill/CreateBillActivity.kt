package org.butterbrot.heve.ubill

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import kotlinx.android.synthetic.main.activity_bill_list.*
import kotlinx.android.synthetic.main.activity_create_bill.*
import kotlinx.android.synthetic.main.content_create_bill.*
import org.butterbrot.heve.ubill.entity.Bill


class CreateBillActivity : BoxActivity<Bill>(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_bill)
        setSupportActionBar(toolbar)

        save.setOnClickListener { _ ->
            box.put(Bill(name.text.trim().toString() , listOf(), mutableListOf()))
            finish()
        }

        add_fellows.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    companion object {
        fun call(context: Context){
            context.startActivity(Intent(context, CreateBillActivity::class.java))
        }
    }
}