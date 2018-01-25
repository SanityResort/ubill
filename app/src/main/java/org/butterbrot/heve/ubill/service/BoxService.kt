package org.butterbrot.heve.ubill.service

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import io.objectbox.Box
import org.butterbrot.heve.ubill.BillApplication
import org.butterbrot.heve.ubill.R
import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Item
import org.butterbrot.heve.ubill.entity.Splitting
import org.json.JSONObject
import java.io.Closeable


class BoxService(val activity: Activity) : Closeable {
    private val itemBox: Box<Item> = (activity.application as BillApplication).boxStore.boxFor(Item::class.java)
    private val splittingBox: Box<Splitting> = (activity.application as BillApplication).boxStore.boxFor(Splitting::class.java)
    private val billBox: Box<Bill> = (activity.application as BillApplication).boxStore.boxFor(Bill::class.java)

    override fun close() {
        billBox.closeThreadResources()
        itemBox.closeThreadResources()
        splittingBox.closeThreadResources()
    }

    fun deleteBill(bill: Bill) {
        AlertDialog.Builder(activity).setTitle(R.string.title_dialog_clear_bill)
                .setMessage(activity.getString(R.string.message_dialog_delete_bill, bill.name))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    splittingBox.remove(bill.items.flatMap { it.splittings })
                    itemBox.remove(bill.items)
                    billBox.remove(bill)
                    activity.finish()
                }.setNegativeButton(android.R.string.no, null).show()

    }

    fun clearBill(bill: Bill) {
        AlertDialog.Builder(activity).setTitle(R.string.title_dialog_clear_bill)
                .setMessage(activity.getString(R.string.message_dialog_clear_bill, bill.name))
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    bill.items.clear()
                    splittingBox.remove(bill.items.flatMap { it.splittings })
                    itemBox.remove(bill.items)
                    billBox.put(bill)
                    activity.finish()
                }.setNegativeButton(android.R.string.no, null).show()

    }

    fun exportBill(billName: String, billJson: String): String {
        val filename = "%s_%d.ubill".format(billName, System.currentTimeMillis())
        activity.openFileOutput(filename, Context.MODE_PRIVATE).use { it.write(billJson.toByteArray()) }
        return filename
    }

}