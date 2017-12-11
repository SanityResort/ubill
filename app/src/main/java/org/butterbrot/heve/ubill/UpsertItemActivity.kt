package org.butterbrot.heve.ubill

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.CheckBox
import io.objectbox.Box
import kotlinx.android.synthetic.main.activity_upsert_item.*
import kotlinx.android.synthetic.main.content_upsert_item.*
import org.butterbrot.heve.ubill.entity.*

class UpsertItemActivity : BoxActivity<Bill>() {
    override val menuId: Int
        get() = R.menu.menu_upsert_item
    override val layoutId: Int
        get() = R.layout.activity_upsert_item

    private lateinit var bill: Bill
    private lateinit var participants: List<Fellow>
    private lateinit var splits: IntArray
    private lateinit var backingItem: Item
    private var editMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getLongExtra(InterfaceConstants.PARAM_BILL, 0)
        bill = box[id]
        populateItem(intent.getLongExtra(InterfaceConstants.PARAM_ITEM, 0))
        splitEvenly.setOnClickListener({ view -> if (!(view as CheckBox).isChecked) {
            callSplitActivity()
        } })
    }

    private fun populateItem(itemId: Long) {
        editMode = itemId > 0
        backingItem = bill.items.find { it.id == itemId } ?: Item()
        if (editMode) {
            participants = backingItem.splittings.map { it.fellow.target }
            splits = backingItem.splittings.map { it.amount * -1 }.toIntArray()
            val payerIndex = payerIndex()
            val nonPayerSum = splits.filterIndexed { index, value ->  index != payerIndex}.foldRight(0, {value, accumulator -> accumulator + value} )
            splits[payerIndex] = backingItem.sum - nonPayerSum
        } else {
            val fellowIds = intent.getLongArrayExtra(InterfaceConstants.PARAM_FELLOWS)
            splits = kotlin.IntArray(fellowIds.size)
            participants = bill.fellows.filter { fellowIds.contains(it.id) }.sortedBy { it.name }
        }
        sum.setNumber(backingItem.sum)
        name.setText(backingItem.name)
        splitEvenly.isChecked = backingItem.splitEvenly
        createPayerAdapter()
    }

    private fun payerIndex() = participants.indexOf(backingItem.payer.target)

    private fun createPayerAdapter() {
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, participants.map { it.name })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        payer.adapter = adapter
        payer.setSelection(participants.indexOf(backingItem.payer.target))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_item -> {
                val itemName = name.text.trim().toString()
                when {
                    itemName.isEmpty() ->
                        Snackbar.make(toolbar, R.string.error_item_name_empty, Snackbar.LENGTH_SHORT).show()
                    !editMode && bill.items.map { it.name }.contains(itemName) ->
                        Snackbar.make(toolbar, R.string.error_item_name_duplicate, Snackbar.LENGTH_SHORT).show()
                    else -> {
                        setItemFields(itemName)
                        if (editMode) {
                            val itemBox = (application as BillApplication).boxStore.boxFor(Item::class.java)
                            itemBox.put(backingItem)
                            itemBox.closeThreadResources()

                        } else {
                            bill.add(backingItem)
                        }
                        box.put(bill)
                        setResult(InterfaceConstants.RESULT_SUCCESS)
                        finish()
                    }
                }
                true
            }
            R.id.split -> {
                callSplitActivity()
                true
            }
            R.id.delete_item -> {
                bill.items.remove(backingItem)
                box.put(bill)
                val itemBox = (application as BillApplication).boxStore.boxFor(Item::class.java)
                itemBox.remove(backingItem)
                itemBox.closeThreadResources()
                deleteSplittings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun callSplitActivity() {
        SplitActivity.call(this, participants.map { it.name }.toTypedArray(), splits, sum.getNumber())
    }

    private fun setItemFields(itemName: String) {
        val totalAmount = sum.getNumber()
        val payingParticipant = participants[payer.selectedItemPosition]
        val splittings: List<Splitting> = if (splitEvenly.isChecked) {
            val splitAmount: Int = Math.round(totalAmount.toDouble() / participants.size).toInt()
            val payerAmount: Int = totalAmount - splitAmount
            participants.map {
                Splitting(it, when (it) {
                    payingParticipant -> payerAmount
                    else -> -splitAmount
                })
            }
        } else {
            participants.mapIndexed { index, participant ->
                Splitting(participant, when (participant) {
                    payingParticipant -> totalAmount - splits[index]
                    else -> -splits[index]
                })
            }
        }
        backingItem.name = itemName
        backingItem.sum = totalAmount
        backingItem.payer.target = payingParticipant
        backingItem.splitEvenly = splitEvenly.isChecked
        deleteSplittings()
        backingItem.updateSplittings(splittings)
    }

    private fun deleteSplittings() {
        val splittingBox: Box<Splitting> = (application as BillApplication).boxStore.boxFor(Splitting::class.java)
        splittingBox.remove(backingItem.splittings)
        splittingBox.closeThreadResources()
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
            val intent = Intent(context, UpsertItemActivity::class.java)
            intent.putExtra(InterfaceConstants.PARAM_BILL, billId)
            intent.putExtra(InterfaceConstants.PARAM_FELLOWS, fellowIds)
            context.startActivityForResult(intent, InterfaceConstants.RC_CREATE_ITEM)
        }

        fun call(context: Activity, billId: Long, itemId: Long) {
            val intent = Intent(context, UpsertItemActivity::class.java)
            intent.putExtra(InterfaceConstants.PARAM_BILL, billId)
            intent.putExtra(InterfaceConstants.PARAM_ITEM, itemId)
            context.startActivityForResult(intent, InterfaceConstants.RC_EDIT_ITEM)
        }
    }
}
