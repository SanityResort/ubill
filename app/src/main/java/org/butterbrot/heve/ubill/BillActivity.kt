package org.butterbrot.heve.ubill

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TableRow
import android.widget.TextView
import kotlinx.android.synthetic.main.content_bill.*
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Fellow

class BillActivity : BoxActivity<Bill>() {

    override val layoutId: Int
        get() = R.layout.activity_bill

    override val menuId: Int
        get() = R.menu.menu_bill

    private lateinit var bill: Bill
    private var id: Long = 0
    private val itemViews: MutableMap<Long, TableRow> = mutableMapOf()
    private lateinit var nameRow: TableRow
    private lateinit var totalsRow: TableRow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = intent.getLongExtra(InterfaceConstants.PARAM_BILL, 0)

        nameRow = TableRow(this)
        totalsRow = TableRow(this)
        nameRow.tag="sticky"
        totalsRow.tag="sticky"
    }

    override fun onResume() {
        super.onResume()
        bill = box[id]
        supportActionBar?.title = getString(R.string.title_activity_bill, bill.name)
        val fellows = bill.fellowsRelation.sortedBy { it.name }
        val amounts: MutableMap<Fellow, Int> = fellows.associate { it -> it to 0 }.toMutableMap()
        var remainder: Int = 0
        bill.items.forEach {
            remainder += it.remainder
            it.splittings.forEach {
                val fellow = it.fellowRelation.target
                val oldAmount = amounts[fellow] ?: 0
                amounts.put(fellow, oldAmount.plus(it.amount))
            }
        }

        nameRow.removeAllViews()
        totalsRow.removeAllViews()
        table.removeAllViews()

        nameRow.addView(TextView(this))
        totalsRow.addView(createCell(getString(R.string.label_total)))

        fellows.forEach {
            nameRow.addView(createCell(it.name))
            totalsRow.addView(createCell((amounts.get(it)?: 0).toString()))
        }

        table.addView(nameRow)
        table.addView(totalsRow)

        bill.items.forEach{
            val row = TableRow(this)
            row.addView(createCell(it.name))
            it.splittings.sortedBy { fellows.indexOf(it.fellowRelation.target) }.forEach {
                row.addView(createCell(it.amount.toString()))
            }
            table.addView(row)
        }
    }

    private fun createCell(text: String): TextView {
        val view = TextView(this)
        view.text = text
        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_bill -> {
                box.remove(bill)
                finish()
                true
            }
            R.id.edit_bill -> {
                EditBillActivity.call(this, bill.id)
                true
            }
            R.id.create_item -> {
                CreateItemActivity.call(this, bill.id)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
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
