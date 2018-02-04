package org.butterbrot.heve.ubill

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_upsert_item.*
import kotlinx.android.synthetic.main.content_bill_list.*
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.service.BoxService
import org.butterbrot.heve.ubill.service.JsonService


class BillListActivity : BoxActivity<Bill>() {

    override val layoutId: Int
        get() = R.layout.activity_bill_list

    override val menuId: Int
        get() = R.menu.menu_bill_list

    private lateinit var bills: List<Bill>
    private lateinit var boxService: BoxService
    private val jsonService: JsonService = JsonService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bill_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            BillActivity.call(this, bills[pos].id)
        }

        registerForContextMenu(bill_list)

        bill_list.setOnCreateContextMenuListener({ contextMenu: ContextMenu, view: View, contextMenuInfo: ContextMenu.ContextMenuInfo ->
            if (view == bill_list) {
                menuInflater.inflate(R.menu.menu_bill_list_export, contextMenu)
                contextMenu.setHeaderTitle(bills[(contextMenuInfo as AdapterView.AdapterContextMenuInfo).position].name)
            }
        })
        boxService = BoxService(this)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val index = (item?.menuInfo as AdapterView.AdapterContextMenuInfo).position
        val itemId: Int = item.itemId ?: 0
        val bill = bills[index]
        when (itemId) {
            R.id.export_bill -> {
                val json = jsonService.serializeBill(bill)
                val fileName = boxService.exportBill(bill.name, json)
                Snackbar.make(toolbar, getString(R.string.message_exported_bill, bill.name, fileName), Snackbar.LENGTH_LONG).show()
            }
            R.id.delete_bill -> {
                boxService.deleteBill(bill, true)
            }
            R.id.clear_bill -> {
                boxService.clearBill(bill, false)
            }
            else -> {
            }
        }

        return true
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

    override fun onDestroy() {
        super.onDestroy()
        boxService.close()
    }
}
