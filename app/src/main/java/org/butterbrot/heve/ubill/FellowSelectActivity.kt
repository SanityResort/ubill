package org.butterbrot.heve.ubill

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_bill_list.*
import kotlinx.android.synthetic.main.content_fellow_select.*
import org.butterbrot.heve.ubill.entity.Fellow

class FellowSelectActivity : BoxActivity<Fellow>() {

    lateinit private var allFellows: List<Fellow>
    private var passedFellowIds: LongArray = kotlin.LongArray(0)
    private var fellowsFromSplittings = kotlin.LongArray(0)

    override val layoutId: Int
        get() = R.layout.activity_fellow_select

    override val menuId: Int
        get() = R.menu.menu_fellow_select

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init internal lists
        allFellows = box.all.sortedBy { it.name }

        // read fellow ids passed from calling activity
        if (intent?.extras?.containsKey(InterfaceConstants.PARAM_FELLOWS) == true) {
            passedFellowIds = intent.extras.getLongArray(InterfaceConstants.PARAM_FELLOWS)
        }

        if (intent?.extras?.containsKey(InterfaceConstants.PARAM_FELLOWS_IN_SPLITTINGS) == true) {
            fellowsFromSplittings = intent.extras.getLongArray(InterfaceConstants.PARAM_FELLOWS_IN_SPLITTINGS)
        }

        initAdapter()

        // register listener to map checkbox status to boolean area
        fellow_list.onItemClickListener = AdapterView.OnItemClickListener { _, view, pos, _ ->
            val id = allFellows[pos].id
            if ((view as CheckedTextView).isChecked) {
                passedFellowIds = passedFellowIds.plus(id)
            } else {
                if (fellowsFromSplittings.contains(id)) {
                    Snackbar.make(toolbar, getString(R.string.error_fellow_referenced_in_splittings, view.text),
                            Snackbar.LENGTH_SHORT).show()
                    fellow_list.setItemChecked(pos,true)
                } else {
                    passedFellowIds = passedFellowIds.filterNot { it == id }.toLongArray()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // if there has been a fellow added, we have to reinitialize the internal arrays and add
        // the new id to the passed fellows so it gets checked automatically
        if (InterfaceConstants.RESULT_SUCCESS == resultCode && InterfaceConstants.RC_CREATE_FELLOW == requestCode) {
            val createdFellows = data?.extras?.getLongArray(InterfaceConstants.RESULT_KEY) ?: kotlin.LongArray(0)
            if (createdFellows.isNotEmpty()) {
                allFellows = box.all.sortedBy { it.name }
                passedFellowIds = passedFellowIds.plus(createdFellows)
                initAdapter()

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.create_fellow -> {
                CreateFellowActivity.call(this)
                return true
            }
            R.id.save_fellow_selection -> {
                val result = Intent()
                result.putExtra(InterfaceConstants.RESULT_KEY, passedFellowIds)
                        //allFellows.filterIndexed { index, _ -> selectedFellows[index] }.map { it.id }.toLongArray())
                setResult(InterfaceConstants.RESULT_SUCCESS, result)
                finish()
                return true
            }
            R.id.list_fellows -> {
                FellowListActivity.call(this)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initAdapter() {
        fellow_list.adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,
                allFellows.map { it.name }) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                (parent as ListView).checkedItemPositions.append(position, passedFellowIds.contains(allFellows[position].id))
                return super.getView(position, convertView, parent)
            }
        }
    }

    companion object {

        fun call(context: Activity, ids: LongArray, fellowsInSplittings: LongArray) {
            val intent = Intent(context, FellowSelectActivity::class.java)
            intent.putExtra(InterfaceConstants.PARAM_FELLOWS, ids)
            intent.putExtra(InterfaceConstants.PARAM_FELLOWS_IN_SPLITTINGS, fellowsInSplittings)
            context.startActivityForResult(intent, InterfaceConstants.RC_SELECT_FELLOWS)
        }
    }
}
