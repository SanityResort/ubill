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
    private lateinit var splits: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getLongExtra(InterfaceConstants.PARAM_BILL, 0)
        bill = box[id]
        val fellowIds = intent.getLongArrayExtra(InterfaceConstants.PARAM_FELLOWS)
        splits = kotlin.IntArray(fellowIds.size)
        participants = bill.fellows.filter { fellowIds.contains(it.id) }.sortedBy { it.name }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, participants.map { it.name })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        payer.adapter = adapter
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
            R.id.split -> {
                SplitActivity.call(this, participants.map { it.name }.toTypedArray(), splits, sum.getNumber())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createItem(itemName: String): Item {
        val totalAmount = sum.getNumber()
        val payingParticipant: Fellow = participants[payer.selectedItemPosition]
        if (splitEvenly.isChecked) {
            val splitAmount: Int = Math.round(totalAmount.toDouble() / participants.size).toInt()
            val payerAmount: Int = totalAmount - splitAmount
            return Item(itemName, totalAmount, splitEvenly.isChecked, participants.map {
                Splitting(it, when (it) {
                    payingParticipant -> payerAmount
                    else -> -splitAmount
                })
            })
        } else {
            return Item(itemName, totalAmount, splitEvenly.isChecked, participants.mapIndexed { index, participant ->
                Splitting(participant, when (participant) {
                    payingParticipant -> totalAmount - splits[index]
                    else -> -splits[index]
                })
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (InterfaceConstants.RESULT_SUCCESS == resultCode && InterfaceConstants.RC_SPLIT == requestCode) {
            if (data != null) {
                splits = data.getIntArrayExtra(InterfaceConstants.RESULT_KEY)
                splitEvenly.isChecked = false
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
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
