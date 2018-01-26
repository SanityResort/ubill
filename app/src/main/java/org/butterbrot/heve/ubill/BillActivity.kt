package org.butterbrot.heve.ubill

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import io.objectbox.Box
import org.butterbrot.heve.scrolltable.ScrollTable
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Fellow
import org.butterbrot.heve.ubill.entity.Item
import org.butterbrot.heve.ubill.entity.Splitting
import org.butterbrot.heve.ubill.service.BoxService
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
    private lateinit var scrollTable: ScrollTable
    private lateinit var boxService: BoxService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        boxService = BoxService(this)
        itemBox = (application as BillApplication).boxStore.boxFor(Item::class.java)
        splittingBox = (application as BillApplication).boxStore.boxFor(Splitting::class.java)
        id = intent.getLongExtra(InterfaceConstants.PARAM_BILL, 0)

        scrollTable = findViewById(R.id.scrollTable)
        scrollTable.dummyView = { createCell("", Gravity.CENTER) }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.getItem(0)?.setEnabled(bill.fellows.isNotEmpty())
        return super.onPrepareOptionsMenu(menu)
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

        val headerRow: MutableList<View> = mutableListOf()
        headerRow.add(scrollTable.dummyView())
        val footerRow: MutableList<View> = mutableListOf()
        footerRow.add(createCell(getString(R.string.label_bill_total), Gravity.START))


        fellows.forEach {
            headerRow.add(createCell(it.name, Gravity.CENTER_HORIZONTAL))
            footerRow.add(createNumberCell(amounts[it] ?: 0))
        }
        scrollTable.addRow(headerRow)

        // TODO remove loop
        (1..1).forEach {
            bill.items.forEach { item ->
                val itemRow = mutableListOf<View>()
                itemRow.add(createCell(item.name, Gravity.START))
                fellows.forEach { fellow ->
                    val amount = item.splittings.firstOrNull { fellow == it.fellow.target }?.amount ?: 0
                    itemRow.add(createNumberCell(amount))
                }
                itemRow.forEach {
                    it.setOnClickListener { _ ->
                        UpsertItemActivity.call(this, bill.id, item.id)
                    }
                }
                scrollTable.addRow(itemRow)
            }
        }
        scrollTable.addRow(footerRow)
    }

    private fun createNumberCell(amount: Int): TextView {
        val view = NumberView(this, null, 0)
        view.setNumber(amount)
        return styleView(view, Gravity.END)
    }

    private fun createCell(text: String, gravity: Int): TextView {
        val view = TextView(this, null, 0)
        view.text = text
        return styleView(view, gravity)
    }

    private fun styleView(view: TextView, gravity: Int): TextView {
        view.gravity = gravity
        @Suppress("DEPRECATION")
        view.setBackgroundColor(resources.getColor(R.color.colorBackground))
        view.setPadding(4, 4, 4, 4)
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_bill -> {
                boxService.deleteBill(bill)
                true
            }
            R.id.clear_bill -> {
                boxService.clearBill(bill)
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
        boxService.close()
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
