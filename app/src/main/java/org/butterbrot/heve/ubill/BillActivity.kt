package org.butterbrot.heve.ubill

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TableRow
import android.widget.TextView
import io.objectbox.Box
import kotlinx.android.synthetic.main.content_bill.*
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Fellow
import org.butterbrot.heve.ubill.entity.Item
import org.butterbrot.heve.ubill.entity.Splitting

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
    private lateinit var itemBox: Box<Item>
    private lateinit var splittingBox: Box<Splitting>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemBox = (application as BillApplication).boxStore.boxFor(Item::class.java)
        splittingBox = (application as BillApplication).boxStore.boxFor(Splitting::class.java)
        id = intent.getLongExtra(InterfaceConstants.PARAM_BILL, 0)

        nameRow = TableRow(this)
        totalsRow = TableRow(this)
        nameRow.tag = "sticky"
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
            totalsRow.addView(createCell((amounts.get(it) ?: 0).toString()))
        }

        table.addView(nameRow)
        table.addView(totalsRow)

        bill.items.forEach {
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
                CreateItemActivity.call(this, bill.id)
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
