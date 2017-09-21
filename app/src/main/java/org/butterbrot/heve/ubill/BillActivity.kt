package org.butterbrot.heve.ubill

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_bill.*
import org.butterbrot.heve.ubill.entity.Bill

class BillActivity : BoxActivity<Bill>() {

    override val layoutId: Int
        get() = R.layout.activity_bill

    override val menuId: Int
        get() = R.menu.menu_bill

    private lateinit var bill: Bill

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id: Long = intent.getLongExtra(InterfaceConstants.PARAM_BILL, 0)
        bill = box[id]
        actionBar.title = getString(R.string.title_activity_bill, bill.name)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_bill -> {
                box.remove(bill)
                finish()
                return true
            }
            R.id.update_bill -> {
                box.put(bill)
                finish()
                return true
            }
            R.id.edit_bill -> {
                EditBillActivity.call(this, bill.id)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {
        fun call(context: Context, id: Long) {
            val intent = Intent(context, BillActivity::class.java)
            intent.putExtra(InterfaceConstants.PARAM_BILL, id)
            context.startActivity(intent)
        }
    }

}
