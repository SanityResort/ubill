package org.butterbrot.heve.ubill

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_fellow_list.*
import kotlinx.android.synthetic.main.content_fellow_list.*
import org.butterbrot.heve.ubill.entity.Fellow

class FellowListActivity : BoxActivity<Fellow>() {

    lateinit private var allFellows: List<Fellow>
    lateinit private var selectedFellows: BooleanArray
    private var passedFellowIds: LongArray = kotlin.LongArray(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fellow_list)
        setSupportActionBar(toolbar)

        if (intent?.extras?.containsKey(InterfaceConstants.PARAM_FELLOWS) == true) {
            passedFellowIds = intent.extras.getLongArray(InterfaceConstants.PARAM_FELLOWS)
        }

        fellow_list.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, pos, _ ->
            selectedFellows.set(pos, (view as CheckedTextView).isChecked)
        }

        add_fellow.setOnClickListener { _ ->
            CreateFellowActivity.call(this)
        }

        select_fellows.setOnClickListener { _ ->
            val result = Intent()
            result.putExtra(InterfaceConstants.RESULT_KEY, allFellows.
                    filterIndexed { index, _ -> selectedFellows[index] }.map { it.getId() }.toLongArray())
            setResult(InterfaceConstants.RESULT_SUCCESS, result)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (InterfaceConstants.RESULT_SUCCESS == resultCode && InterfaceConstants.RC_CREATE_FELLOW == requestCode) {
            passedFellowIds = data?.extras?.getLongArray(InterfaceConstants.RESULT_KEY) ?: kotlin.LongArray(0)
        }
    }

    override fun onResume() {
        super.onResume()
        allFellows = box.all.sortedBy { it.name }
        selectedFellows = kotlin.BooleanArray(allFellows.size)

        evaluatePassedFellows()

        fellow_list.adapter =
                object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,
                        allFellows.map { it.name }) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                        val view = super.getView(position, convertView, parent)
                        (parent as ListView).checkedItemPositions.append(position, selectedFellows[position])
                        return view
                    }
                }
    }

    private fun evaluatePassedFellows() {
        allFellows.forEachIndexed { index, fellow ->
            if (passedFellowIds.contains(fellow.getId())) {
                selectedFellows[index] = true
            }
        }

        passedFellowIds = LongArray(0)
    }

    companion object {

        fun call(context: Activity) {
            context.startActivityForResult(Intent(context, FellowListActivity::class.java), InterfaceConstants.RC_SELECT_FELLOWS)
        }
    }
}
