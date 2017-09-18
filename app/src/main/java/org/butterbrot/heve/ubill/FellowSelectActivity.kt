package org.butterbrot.heve.ubill

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.ListView
import kotlinx.android.synthetic.main.content_fellow_select.*
import org.butterbrot.heve.ubill.entity.Fellow

class FellowSelectActivity : BoxActivity<Fellow>() {

    lateinit private var allFellows: List<Fellow>
    lateinit private var selectedFellows: BooleanArray
    private var passedFellowIds: LongArray = kotlin.LongArray(0)

    override val layoutId: Int
        get() = R.layout.activity_fellow_select

    override val menuId: Int
        get() = R.menu.menu_fellow_select

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init internal lists
        allFellows = box.all.sortedBy { it.name }
        selectedFellows = kotlin.BooleanArray(allFellows.size)

        // read fellow ids passed from calling activity
        if (intent?.extras?.containsKey(InterfaceConstants.PARAM_FELLOWS) == true) {
            passedFellowIds = intent.extras.getLongArray(InterfaceConstants.PARAM_FELLOWS)
        }

        evaluatePassedFellows()

        fellow_list.adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,
                allFellows.map { it.name }) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getView(position, convertView, parent)
                (parent as ListView).checkedItemPositions.append(position, selectedFellows[position])
                return view
            }
        }

        // register listener to map checkbox status to boolean area
        fellow_list.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, pos, _ ->
            selectedFellows.set(pos, (view as CheckedTextView).isChecked)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // if there has been a fellow added, we have to reinitialize the internal arrays and add
        // the new id to the passed fellows so it gets checked automatically
        if (InterfaceConstants.RESULT_SUCCESS == resultCode && InterfaceConstants.RC_CREATE_FELLOW == requestCode) {
            allFellows = box.all.sortedBy { it.name }
            selectedFellows = kotlin.BooleanArray(allFellows.size)
            passedFellowIds.plus(data?.extras?.getLongArray(InterfaceConstants.RESULT_KEY) ?: kotlin.LongArray(0))
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
            R.id.add_fellows -> {
                val result = Intent()
                result.putExtra(InterfaceConstants.RESULT_KEY, allFellows.
                        filterIndexed { index, _ -> selectedFellows[index] }.map { it.getId() }.toLongArray())
                setResult(InterfaceConstants.RESULT_SUCCESS, result)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // read passed fellow ids and set fields in the boolean array accordingly
    private fun evaluatePassedFellows() {
        allFellows.forEachIndexed { index, fellow ->
            if (passedFellowIds.contains(fellow.getId())) {
                selectedFellows[index] = true
            }
        }

        passedFellowIds = LongArray(0)
    }

    companion object {

        fun call(context: Activity, ids: LongArray) {
            val intent = Intent(context, FellowSelectActivity::class.java)
            intent.putExtra(InterfaceConstants.PARAM_FELLOWS, ids)
            context.startActivityForResult(intent, InterfaceConstants.RC_SELECT_FELLOWS)
        }
    }
}
