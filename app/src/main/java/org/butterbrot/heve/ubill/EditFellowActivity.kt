package org.butterbrot.heve.ubill

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import io.objectbox.Box
import io.objectbox.query.QueryFilter
import kotlinx.android.synthetic.main.activity_bill_list.*
import kotlinx.android.synthetic.main.content_edit_fellow.*
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Fellow
import org.butterbrot.heve.ubill.entity.Fellow_

class EditFellowActivity : BoxActivity<Fellow>() {
    override val menuId: Int
        get() = R.menu.menu_edit_fellow
    override val layoutId: Int
        get() = R.layout.activity_edit_fellow

    private lateinit var fellow: Fellow

    private lateinit var billBox: Box<Bill>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getLongExtra(InterfaceConstants.PARAM_FELLOW, -1)
        fellow = box[id]
        name.setText(fellow.name)
        billBox = (application as BillApplication).boxStore.boxFor(Bill::class.java)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_fellow -> {
                val newName: String = name.text.trim().toString()
                if (newName.isNotBlank()) {
                    if (fellow.name.equals(newName) || box.query().equal(Fellow_.name, newName).build().find().isEmpty()) {
                        fellow.name = newName
                        box.put(fellow)
                        finish()
                    } else {
                        Snackbar.make(toolbar, R.string.error_fellow_name_duplicate, Snackbar.LENGTH_SHORT).show()
                    }
                } else {
                    Snackbar.make(toolbar, R.string.error_fellow_name_empty, Snackbar.LENGTH_SHORT).show()
                }
                return true
            }
            R.id.delete_fellow -> {
                val referencingBills: List<String> = referencedInBills(fellow)

                if (referencingBills.isEmpty()) {
                    AlertDialog.Builder(this).setTitle(R.string.title_dialog_delete_fellow)
                            .setMessage(getString(R.string.message_dialog_delete_fellow, fellow.name))
                            .setPositiveButton(android.R.string.yes) { _, _ ->
                                box.remove(fellow)
                                finish()
                            }.setNegativeButton(android.R.string.no, null).show()
                } else {
                    Snackbar.make(toolbar, getString(R.string.error_fellow_referenced_in_bills,
                            fellow.name, referencingBills.joinToString(",")), Snackbar.LENGTH_SHORT).show()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun referencedInBills(fellow: Fellow):List<String> {
        return billBox.query().filter(object : QueryFilter<Bill> {
            override fun keep(bill: Bill): Boolean {
                return bill.hasFellow(fellow)
            }
        }).build().find().map { it.name }
    }

    override fun onDestroy() {
        super.onDestroy()
        billBox.closeThreadResources()
    }

    companion object {
        fun call(context: Context, id: Long) {
            val intent = Intent(context, EditFellowActivity::class.java)
            intent.putExtra(InterfaceConstants.PARAM_FELLOW, id)
            context.startActivity(intent)
        }
    }
}
