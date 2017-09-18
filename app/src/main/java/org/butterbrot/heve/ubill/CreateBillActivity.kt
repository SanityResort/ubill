package org.butterbrot.heve.ubill

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import io.objectbox.Box
import kotlinx.android.synthetic.main.content_create_bill.*
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Fellow
import org.butterbrot.heve.ubill.entity.Fellow_


class CreateBillActivity : BoxActivity<Bill>() {

    override val layoutId: Int
        get() = R.layout.activity_create_bill

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
                box.put(Bill(name.text.trim().toString(), fellows))
                finish()
                return true
            }
            R.id.add_fellows -> {
                FellowSelectActivity.call(this, kotlin.LongArray(0).plus(fellows.map { it.id }))
                return true
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