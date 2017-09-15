package org.butterbrot.heve.ubill

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import kotlinx.android.synthetic.main.activity_create_fellow.*
import kotlinx.android.synthetic.main.content_create_fellow.*
import org.butterbrot.heve.ubill.entity.Fellow
import org.butterbrot.heve.ubill.entity.Fellow_

class CreateFellowActivity : BoxActivity<Fellow>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_fellow)
        setSupportActionBar(toolbar)

        save.setOnClickListener { view ->
            val name = name.text.trim().toString()
            if (name.isNotBlank()) {
                if (box.query().equal(Fellow_.name, name).build().find().isEmpty()) {
                    val id = box.put(Fellow(name))
                    val intent = Intent()
                    intent.putExtra(InterfaceConstants.RESULT_KEY, kotlin.LongArray(0).plus(id))
                    setResult(InterfaceConstants.RESULT_SUCCESS, intent)
                    finish()
                } else {
                    Snackbar.make(view, R.string.error_fellow_name_duplicate, Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(view, R.string.error_fellow_name_empty, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun call(context: Activity){
            context.startActivityForResult(Intent(context, CreateFellowActivity::class.java), InterfaceConstants.RC_CREATE_FELLOW)
        }
    }
}
