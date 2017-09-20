package org.butterbrot.heve.ubill

import android.app.Activity
import android.content.Intent
import android.support.design.widget.Snackbar
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_create_fellow.*
import kotlinx.android.synthetic.main.content_create_fellow.*
import org.butterbrot.heve.ubill.entity.Fellow
import org.butterbrot.heve.ubill.entity.Fellow_

class CreateFellowActivity : BoxActivity<Fellow>() {

    override val layoutId: Int
        get() = R.layout.activity_create_fellow

    override val menuId: Int
        get() = R.menu.menu_create_fellow

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.save_fellow -> {
                val fellowName = name.text.trim().toString()
                if (fellowName.isNotBlank()) {
                    if (box.query().equal(Fellow_.name, fellowName).build().find().isEmpty()) {
                        val id = box.put(Fellow(fellowName))
                        val intent = Intent()
                        intent.putExtra(InterfaceConstants.RESULT_KEY, kotlin.LongArray(0).plus(id))
                        setResult(InterfaceConstants.RESULT_SUCCESS, intent)
                        finish()
                    } else {
                        Snackbar.make(toolbar, R.string.error_fellow_name_duplicate, Snackbar.LENGTH_SHORT).show()
                    }
                } else {
                    Snackbar.make(toolbar, R.string.error_fellow_name_empty, Snackbar.LENGTH_SHORT).show()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun call(context: Activity){
            context.startActivityForResult(Intent(context, CreateFellowActivity::class.java), InterfaceConstants.RC_CREATE_FELLOW)
        }
    }
}
