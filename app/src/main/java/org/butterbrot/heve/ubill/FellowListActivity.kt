package org.butterbrot.heve.ubill

import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.content_fellow_select.*
import org.butterbrot.heve.ubill.entity.Fellow

class FellowListActivity : BoxActivity<Fellow>() {

    lateinit private var allFellows: List<Fellow>

    override val layoutId: Int
        get() = R.layout.activity_fellow_list

    override val menuId: Int
        get() = R.menu.menu_fellow_list

    override fun onResume() {
        super.onResume()

        // init internal lists
        allFellows = box.all.sortedBy { it.name }

        fellow_list.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allFellows.map { it.name })
        // register listener to map checkbox status to boolean area
        fellow_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            EditFellowActivity.call(this, allFellows[pos].id)
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {

        fun call(context: Activity) {
            val intent = Intent(context, FellowListActivity::class.java)
            context.startActivity(intent)
        }
    }
}