package org.butterbrot.heve.ubill

import android.os.Bundle
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.content_bill_list.*
import org.butterbrot.heve.ubill.entity.Bill

class BillListActivity : BoxActivity<Bill>() {

    override val layoutId: Int
        get() = R.layout.activity_bill_list

    override val menuId: Int
        get() = R.menu.menu_bill_list

    private lateinit var bills: List<Bill>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bill_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            BillActivity.call(this, bills[pos].id)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.create_bill -> {
                CreateBillActivity.call(this)
                return true
            }
            R.id.list_fellows -> {
                FellowListActivity.call(this)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        bills = box.all
        bill_list.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bills.map { bill -> bill.name })
    }
}
