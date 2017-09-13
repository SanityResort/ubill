package org.butterbrot.heve.ubill

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_bill_list.*
import kotlinx.android.synthetic.main.content_bill_list.*
import org.butterbrot.heve.ubill.entity.Bill

class BillListActivity : BoxActivity<Bill>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_list)
        setSupportActionBar(toolbar)

        new_bill.setOnClickListener { view ->
            box.put(Bill("test" + System.currentTimeMillis(), listOf(), mutableListOf()))
            Snackbar.make(view, "Found " + box.all.size + " elements", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_bill_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        bill_list.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, box.all.map { bill -> bill.name })
    }
}
