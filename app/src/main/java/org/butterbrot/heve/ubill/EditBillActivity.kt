package org.butterbrot.heve.ubill

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_upsert_bill.*
import kotlinx.android.synthetic.main.content_upsert_bill.*
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Bill_

class EditBillActivity : CreateBillActivity() {
    override val menuId: Int
        get() = R.menu.menu_edit_bill

    private lateinit var bill: Bill

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id: Long = intent.getLongExtra(InterfaceConstants.PARAM_BILL, 0)
        bill = box[id]
        actionBar.title = getString(R.string.title_activity_edit_bill, bill.name)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.update_bill -> {
                val billName: String = name.text.trim().toString()
                when {
                    billName.isEmpty() ->
                        Snackbar.make(toolbar, R.string.error_bill_name_empty, Snackbar.LENGTH_SHORT).show()
                    billName != bill.name && box.find(Bill_.name, billName).isNotEmpty() ->
                        Snackbar.make(toolbar, R.string.error_bill_name_duplicate, Snackbar.LENGTH_SHORT).show()
                    else -> {
                        bill.name = billName
                        bill.setFellows(fellows)
                        box.put(bill)
                        finish()
                    }
                }
                return true
            }
            R.id.change_fellows -> {
                FellowSelectActivity.call(this, bill.fellowsRelation.map { it.id }.toLongArray(),
                        bill.fellowsInSplittings())
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {
        fun call(context: Context, id: Long) {
            val intent = Intent(context, EditBillActivity::class.java)
            intent.putExtra(InterfaceConstants.PARAM_BILL, id)
            context.startActivity(intent)
        }
    }
}
