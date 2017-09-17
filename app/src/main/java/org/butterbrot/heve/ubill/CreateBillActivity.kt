package org.butterbrot.heve.ubill

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_create_bill.*
import kotlinx.android.synthetic.main.content_create_bill.*
import org.butterbrot.heve.ubill.entity.Bill


class CreateBillActivity : BoxActivity<Bill>(){

    override val layoutId: Int
        get() = R.layout.activity_create_bill

    override val menuId: Int
        get() = R.menu.menu_create_bill

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.save_bill -> {
                box.put(Bill(name.text.trim().toString() , listOf(), mutableListOf()))
                finish()
                return true
            }
            R.id.add_fellows -> {
                FellowListActivity.call(this)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun call(context: Context){
            context.startActivity(Intent(context, CreateBillActivity::class.java))
        }
    }
}