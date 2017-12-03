package org.butterbrot.heve.ubill

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.ListView
import kotlinx.android.synthetic.main.content_choose_participants.*
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Fellow

class ChooseParticipantsActivity : BoxActivity<Bill>() {
    override val menuId: Int
        get() = R.menu.menu_choose_participants
    override val layoutId: Int
        get() = R.layout.activity_choose_participants

    private lateinit var fellows: List<Fellow>
    private var billId: Long = 0
    private lateinit var selectedIds: List<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billId = intent.getLongExtra(InterfaceConstants.PARAM_BILL, 0)
        fellows = box[billId].fellows.sortedBy { it.name }
        selectedIds = fellows.map { it.id }.toMutableList()

        participants.adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,
                fellows.map { it.name }) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                (parent as ListView).checkedItemPositions.append(position, selectedIds.contains(fellows[position].id))
                return super.getView(position, convertView, parent)
            }
        }
        participants.onItemClickListener = AdapterView.OnItemClickListener { _, view, pos, _ ->
            val id = fellows[pos].id
            if ((view as CheckedTextView).isChecked) {
                selectedIds = selectedIds.plus(id)
            } else {
                selectedIds = selectedIds.filterNot { it == id }

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.choose -> {
                UpsertItemActivity.call(this, billId, selectedIds.toLongArray())
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (InterfaceConstants.RC_CREATE_ITEM == requestCode && InterfaceConstants.RESULT_SUCCESS == resultCode) {
            finish()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        fun call(context: Context, billId: Long) {
            val intent = Intent(context, ChooseParticipantsActivity::class.java)
            intent.putExtra(InterfaceConstants.PARAM_BILL, billId)
            context.startActivity(intent)
        }
    }
}
