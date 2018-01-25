package org.butterbrot.heve.ubill.service

import org.butterbrot.heve.ubill.entity.Bill
import org.butterbrot.heve.ubill.entity.Item
import org.butterbrot.heve.ubill.entity.Splitting
import org.json.JSONArray
import org.json.JSONObject


class JsonService {
    fun serializeBill(bill: Bill): String {
        val root: JSONObject = JSONObject()
        root.put("name", bill.name)
        val fellows: JSONArray = JSONArray(bill.fellows.map { it.name })
        root.put("fellows", fellows)
        val items: JSONArray = JSONArray(bill.items.map { serializeItem(it) })
        root.put("items", items)
        return root.toString()
    }


    private fun serializeItem(item: Item): JSONObject {
        val root: JSONObject = JSONObject()
        root.put("name", item.name)
        root.put("payer", item.payer.target.name)
        root.put("splitEvenly", item.splitEvenly)
        root.put("sum", item.sum)
        val splittings: JSONArray = JSONArray(item.splittings.map { serializeSplitting(it) })
        root.put("splittings", splittings)
        return root;
    }

    private fun serializeSplitting(splitting: Splitting): JSONObject {
        val root: JSONObject = JSONObject()
        root.put("amount", splitting.amount)
        root.put("fellow", splitting.fellow.target.name)
        return root;

    }
}