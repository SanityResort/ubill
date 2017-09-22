package org.butterbrot.heve.ubill

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import android.widget.ArrayAdapter
import io.objectbox.Box
import kotlinx.android.synthetic.main.activity_create_fellow.*
import kotlinx.android.synthetic.main.content_upsert_bill.*
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Bill_
import org.butterbrot.heve.ubill.entity.Fellow
import org.butterbrot.heve.ubill.entity.Fellow_

class CreateBillActivity : BoxActivity<Bill>() {

    override val layoutId: Int
        get() = R.layout.activity_upsert_bill

    override val menuId: Int
        get() = R.menu.menu_create_bill

    private var fellows: List<Fellow> = listOf()

    private lateinit var fellowBox: Box<Fellow>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fellowBox = (application as BillApplication).boxStore.boxFor(Fellow::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (InterfaceConstants.RESULT_SUCCESS == resultCode && InterfaceConstants.RC_SELECT_FELLOWS == requestCode) {
            val fellowIds: LongArray = data?.extras?.getLongArray(InterfaceConstants.RESULT_KEY) ?: kotlin.LongArray(0)
            fellows = fellowBox.query().`in`(Fellow_.id, fellowIds).build().find().sortedBy { it.name }
            fellow_list.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fellows.map { it.name })
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.save_bill -> {
                val billName: String = name.text.trim().toString()
                when {
                    billName.isEmpty() ->
                        Snackbar.make(toolbar, R.string.error_bill_name_empty, Snackbar.LENGTH_SHORT).show()
                    box.find(Bill_.name, billName).isNotEmpty() ->
                        Snackbar.make(toolbar, R.string.error_bill_name_duplicate, Snackbar.LENGTH_SHORT).show()
                    else -> {
                        box.put(Bill(billName, fellows, mutableListOf()))
                        finish()
                    }
                }
                true
            }
            R.id.change_fellows -> {
                FellowSelectActivity.call(this, kotlin.LongArray(0)
                        .plus(fellows.map { it.id }), kotlin.LongArray(0))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fellowBox.closeThreadResources()
    }

    companion object {
        fun call(context: Activity) {
            context.startActivity(Intent(context, CreateBillActivity::class.java))
        }
    }
}