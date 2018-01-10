package org.butterbrot.heve.ubill

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import android.widget.ArrayAdapter
import io.objectbox.Box
import kotlinx.android.synthetic.main.activity_upsert_bill.*
import kotlinx.android.synthetic.main.content_upsert_bill.*
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Bill_
import org.butterbrot.heve.ubill.entity.Fellow
import org.butterbrot.heve.ubill.entity.Fellow_

class EditBillActivity : BoxActivity<Bill>() {

    override val layoutId: Int
        get() = R.layout.activity_upsert_bill

    override val menuId: Int
        get() = R.menu.menu_edit_bill

    private lateinit var bill: Bill
    private lateinit var fellowBox: Box<Fellow>
    private var fellows: List<Fellow> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fellowBox = (application as BillApplication).boxStore.boxFor(Fellow::class.java)
        val id: Long = intent.getLongExtra(InterfaceConstants.PARAM_BILL, 0)
        bill = box[id]
        supportActionBar?.title = getString(R.string.title_activity_edit_bill, bill.name)
        name.setText(bill.name)
        fellows += bill.fellows
        fellow_list.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fellows.map { it.name })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (InterfaceConstants.RESULT_SUCCESS == resultCode && InterfaceConstants.RC_SELECT_FELLOWS == requestCode) {
            val fellowIds: LongArray = data?.extras?.getLongArray(InterfaceConstants.RESULT_KEY) ?: kotlin.LongArray(0)
            fellows = fellowBox.query().`in`(Fellow_.id, fellowIds).build().find().sortedBy { it.name }
            fellow_list.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fellows.map { it.name })
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

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
                FellowSelectActivity.call(this, bill.fellows.map { it.id }.toLongArray(),
                        bill.fellowsInSplittings())
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fellowBox.closeThreadResources()
    }

    companion object {
        fun call(context: Context, id: Long) {
            val intent = Intent(context, EditBillActivity::class.java)
            intent.putExtra(InterfaceConstants.PARAM_BILL, id)
            context.startActivity(intent)
        }
    }
}
