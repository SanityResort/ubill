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

    override val layoutId: Int
        get() = R.layout.activity_bill_list

    override val menuId: Int
        get() = R.menu.menu_bill_list

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.create_bill -> {
                CreateBillActivity.call(this)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        bill_list.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, box.all.map { bill -> bill.name })
    }
}
