package org.butterbrot.heve.ubill

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import io.objectbox.Box
import kotlinx.android.synthetic.main.content_bill.*
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Fellow
import org.butterbrot.heve.ubill.entity.Item
import org.butterbrot.heve.ubill.entity.Splitting
import org.butterbrot.heve.ubill.view.NumberView
import android.widget.LinearLayout



class BillActivity : BoxActivity<Bill>() {

    override val layoutId: Int
        get() = R.layout.activity_bill

    override val menuId: Int
        get() = R.menu.menu_bill

    private lateinit var bill: Bill
    private var id: Long = 0
    private lateinit var nameRow: TableRow
    private lateinit var totalsRow: TableRow
    private lateinit var itemBox: Box<Item>
    private lateinit var splittingBox: Box<Splitting>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemBox = (application as BillApplication).boxStore.boxFor(Item::class.java)
        splittingBox = (application as BillApplication).boxStore.boxFor(Splitting::class.java)
        id = intent.getLongExtra(InterfaceConstants.PARAM_BILL, 0)

        nameRow = createRow()
        totalsRow = createRow()
    }

    private fun createRow() : TableRow {
        val row = TableRow(this)
        row.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
        return row
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

        nameRow.removeAllViews()
        totalsRow.removeAllViews()
        innerTable.removeAllViews()

        fellows.forEach {
            nameRow.addView(createCell(it.name, Gravity.CENTER_HORIZONTAL))
            totalsRow.addView(createNumberCell(amounts[it] ?: 0))
        }

        headerTable.removeAllViews()
        headerTable.addView(nameRow)
        itemTable.removeAllViews()
        bill.items.forEach { item ->
            val amountRow = createRow()
            val itemNameRow = createRow()
            itemNameRow.addView(createCell(item.name, Gravity.START))
            itemTable.addView(itemNameRow)
            fellows.forEach{ fellow ->
                val amount = item.splittings.firstOrNull { fellow == it.fellow.target }?.amount ?: 0
                amountRow.addView(createNumberCell(amount))
            }
            amountRow.setOnClickListener{ _ ->
                UpsertItemActivity.call(this, bill.id, item.id)
            }
            innerTable.addView(amountRow)
        }

        innerTable.addView(totalsRow)
        itemTable.addView(createCell(getString(R.string.label_bill_total), Gravity.START))
        syncWidths()
    }

    private fun syncWidths(){
        val headerRow = (headerTable.getChildAt(0) as TableRow)
        val columnCount = headerRow.childCount
        val rowCount = innerTable.childCount


        var colCounter = 0

        while (colCounter < columnCount) {
            var maxWidth = viewWidth(headerRow.getChildAt(colCounter))
            var rowCounter = 0

            while (rowCounter < rowCount) {
                maxWidth = Math.max(maxWidth, viewWidth((innerTable.getChildAt(rowCounter) as TableRow).getChildAt(colCounter)))
                rowCounter++
            }

            rowCounter = 0
            setViewWidth(headerRow.getChildAt(colCounter), maxWidth)

            while (rowCounter < rowCount) {
                setViewWidth((innerTable.getChildAt(rowCounter) as TableRow).getChildAt(colCounter), maxWidth)
                rowCounter++
            }

            colCounter++
        }

    }

    private fun viewWidth(view: View):Int {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredWidth();
    }

    private fun setViewWidth(view: View, width: Int) {
        val params = view.layoutParams as TableRow.LayoutParams
        params.width = width
    }


    private fun createNumberCell(amount: Int): NumberView {
        val view = NumberView(this)
        view.setNumber(amount)
        view.gravity = Gravity.END
        view.setBackgroundColor(resources.getColor(R.color.colorBackground))
        val llp = TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
        llp.setMargins(2, 1, 2, 1)
        view.setPadding(4, 4, 4, 4)
        view.layoutParams=llp
        return view
    }

    private fun createCell(text: String, gravity: Int): TextView {
        val view = TextView(this)
        view.text = text
        view.gravity = gravity
        view.setBackgroundColor(resources.getColor(R.color.colorBackground))
        val llp = TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
        llp.setMargins(2, 1, 2, 1)
        view.setPadding(4, 4, 4, 4)
        view.layoutParams=llp
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
