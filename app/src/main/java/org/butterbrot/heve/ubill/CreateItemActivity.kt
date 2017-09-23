package org.butterbrot.heve.ubill

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_create_item.*
import kotlinx.android.synthetic.main.content_create_item.*
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Fellow
import org.butterbrot.heve.ubill.entity.Item
import org.butterbrot.heve.ubill.entity.Splitting

class CreateItemActivity : BoxActivity<Bill>() {
    override val menuId: Int
        get() = R.menu.menu_create_item
    override val layoutId: Int
        get() = R.layout.activity_create_item

    private lateinit var bill: Bill
    private lateinit var participants: List<Fellow>
    private lateinit var viewMap: MutableMap<Fellow, View>
    private var splitEvenly: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getLongExtra(InterfaceConstants.PARAM_BILL, 0)
        bill = box[id]
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_item -> {
                val itemName = name.text.trim().toString()
                when {
                    itemName.isEmpty() ->
                        Snackbar.make(toolbar, R.string.error_item_name_empty, Snackbar.LENGTH_SHORT).show()
                    bill.items.map { it.name }.contains(itemName) ->
                        Snackbar.make(toolbar, R.string.error_item_name_duplicate, Snackbar.LENGTH_SHORT).show()
                    else -> {
                        bill.add(createItem(itemName))
                        box.put(bill)
                        setResult(InterfaceConstants.RESULT_SUCCESS)
                        finish()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createItem(itemName: String): Item {
        if (splitEvenly) {
            val totalAmountDouble: Double = (sum.text.toString().toDouble() * 100)
            val totalAmount = totalAmountDouble.toInt()
            val splitAmount: Int = Math.round(totalAmountDouble / participants.size).toInt()
            val payerAmount: Int = totalAmount - splitAmount
            val payingParticipant: Fellow = participants[payer.selectedItemPosition]
            return Item(itemName, totalAmount, participants.map {
                Splitting(it, when (it) {
                    payingParticipant -> payerAmount
                    else -> -splitAmount
                })
            })
        } else {
            TODO()
        }
    }

    override fun onResume() {
        super.onResume()
        val fellowIds = intent.getLongArrayExtra(InterfaceConstants.PARAM_FELLOWS)
        participants = bill.fellows.filter { fellowIds.contains(it.id) }.sortedBy { it.name }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, participants.map { it.name })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        payer.adapter = adapter
    }

    companion object {
        fun call(context: Activity, billId: Long, fellowIds: LongArray) {
            val intent = Intent(context, CreateItemActivity::class.java)
            intent.putExtra(InterfaceConstants.PARAM_BILL, billId)
            intent.putExtra(InterfaceConstants.PARAM_FELLOWS, fellowIds)
            context.startActivityForResult(intent, InterfaceConstants.RC_CREATE_ITEM)
        }
    }
}
