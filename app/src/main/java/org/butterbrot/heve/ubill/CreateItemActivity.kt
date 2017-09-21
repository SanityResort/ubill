package org.butterbrot.heve.ubill

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Item
import org.butterbrot.heve.ubill.entity.Splitting

class CreateItemActivity : BoxActivity<Bill>() {
    override val menuId: Int
        get() = R.menu.menu_create_item
    override val layoutId: Int
        get() = R.layout.activity_create_item

    private lateinit var bill: Bill

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getLongExtra(InterfaceConstants.PARAM_BILL, 0)
        bill = box[id]
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.save_item -> {
                val newItem = Item("test", 10, listOf(Splitting(bill.fellowsRelation[0], 5),
                        Splitting(bill.fellowsRelation[1], 5)))
                bill.add(newItem)
                box.put(bill)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
