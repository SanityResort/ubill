package org.butterbrot.heve.ubill

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_bill_list.*
import kotlinx.android.synthetic.main.content_edit_fellow.*
import org.butterbrot.heve.ubill.entity.Fellow
import org.butterbrot.heve.ubill.entity.Fellow_

class EditFellowActivity : BoxActivity<Fellow>() {
    override val menuId: Int
        get() = R.menu.menu_edit_fellow
    override val layoutId: Int
        get() = R.layout.activity_edit_fellow

    private lateinit var fellow: Fellow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getLongExtra(InterfaceConstants.PARAM_FELLOW, -1)
        fellow = box[id]
        name.setText(fellow.name)
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
                AlertDialog.Builder(this).setTitle(R.string.title_dialog_delete_fellow)
                        .setMessage(getString(R.string.message_dialog_delete_fellow, fellow.name))
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            box.remove(fellow)
                            finish()
                        }.setNegativeButton(android.R.string.no, null).show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun call(context: Context, id: Long) {
            val intent = Intent(context, EditFellowActivity::class.java)
            intent.putExtra(InterfaceConstants.PARAM_FELLOW, id)
            context.startActivity(intent)
        }
    }
}
