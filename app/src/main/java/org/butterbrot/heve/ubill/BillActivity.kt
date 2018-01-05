package org.butterbrot.heve.ubill

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import io.objectbox.Box
import kotlinx.android.synthetic.main.content_bill.*
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Fellow
import org.butterbrot.heve.ubill.entity.Item
import org.butterbrot.heve.ubill.entity.Splitting
import org.butterbrot.heve.ubill.view.NumberView


class BillActivity : BoxActivity<Bill>() {

    override val layoutId: Int
        get() = R.layout.activity_bill

    override val menuId: Int
        get() = R.menu.menu_bill

    private lateinit var bill: Bill
    private var id: Long = 0
    private lateinit var itemBox: Box<Item>
    private lateinit var splittingBox: Box<Splitting>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemBox = (application as BillApplication).boxStore.boxFor(Item::class.java)
        splittingBox = (application as BillApplication).boxStore.boxFor(Splitting::class.java)
        id = intent.getLongExtra(InterfaceConstants.PARAM_BILL, 0)

    }

    override fun onResume() {
        super.onResume()
        bill = box[id]
        supportActionBar?.title = getString(R.string.title_activity_bill, bill.name)
        val fellows = bill.fellows.sortedBy { it.name }
        val amounts: MutableMap<Fellow, Int> = fellows.associate { it -> it to 0 }.toMutableMap()
        bill.items.forEach {
            it.splittings.forEach {
                val fellow = it.fellow.target
                val oldAmount = amounts[fellow] ?: 0
                amounts.put(fellow, oldAmount.plus(it.amount))
            }
        }

        val emptyCell = View(this)

        val headerRow: MutableList<View> = mutableListOf()
        headerRow.add(emptyCell)
        val footerRow: MutableList<View> = mutableListOf()
        footerRow.add(createCell(getString(R.string.label_bill_total), Gravity.START))


        fellows.forEach {
            headerRow.add(createCell(it.name, Gravity.CENTER_HORIZONTAL))
            footerRow.add(createNumberCell(amounts[it] ?: 0))
        }
        scrolltable.addRow(headerRow)

        // TODO remove loop
        (1..10).forEach {
            bill.items.forEach { item ->
                val itemRow = mutableListOf<View>()
                itemRow.add(createCell(item.name, Gravity.START))
                fellows.forEach { fellow ->
                    val amount = item.splittings.firstOrNull { fellow == it.fellow.target }?.amount ?: 0
                    val numberCell = createNumberCell(amount)
                    numberCell.setOnClickListener { _ ->
                        UpsertItemActivity.call(this, bill.id, item.id)
                    }
                    val numberCell2 = createNumberCell(amount)
                    numberCell2.setOnClickListener { _ ->
                        UpsertItemActivity.call(this, bill.id, item.id)
                    }
                    itemRow.add(numberCell)
                }
                scrolltable.addRow(itemRow)
            }
        }
        scrolltable.addRow(footerRow)
    }

    private fun createNumberCell(amount: Int): NumberView {
        val view = NumberView(this)
        view.setNumber(amount)
        view.gravity = Gravity.END
        @Suppress("DEPRECATION")
        view.setBackgroundColor(resources.getColor(R.color.colorBackground))
        view.setPadding(4, 4, 4, 4)
        return view
    }

    private fun createCell(text: String, gravity: Int): TextView {
        val view = TextView(this)
        view.text = text
        view.gravity = gravity
        @Suppress("DEPRECATION")
        view.setBackgroundColor(resources.getColor(R.color.colorBackground))
        view.setPadding(4, 4, 4, 4)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_bill -> {
                AlertDialog.Builder(this).setTitle(R.string.title_dialog_delete_bill)
                        .setMessage(getString(R.string.message_dialog_delete_bill, bill.name))
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            splittingBox.remove(bill.items.flatMap { it.splittings })
                            itemBox.remove(bill.items)
                            box.remove(bill)
                            finish()
                        }.setNegativeButton(android.R.string.no, null).show()
                true
            }
            R.id.edit_bill -> {
                EditBillActivity.call(this, bill.id)
                true
            }
            R.id.create_item -> {
                ChooseParticipantsActivity.call(this, bill.id)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        itemBox.closeThreadResources()
        splittingBox.closeThreadResources()
    }

    companion object {
        fun call(context: Context, id: Long) {
            val intent = Intent(context, BillActivity::class.java)
            intent.putExtra(InterfaceConstants.PARAM_BILL, id)
            context.startActivity(intent)
        }
    }
}
