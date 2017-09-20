package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index
import io.objectbox.relation.ToMany

@Entity
class Bill(@Index val name: String = "", @Transient val fellows: List<Fellow> = listOf(), val items: MutableList<Item> = mutableListOf()) {

    @Id
    var id: Long = 0
    private var remainder: Int = 0
    lateinit var fellowsRelation: ToMany<Fellow>

    init {
        updateRemainder()
        fellowsRelation.addAll(fellows)
    }

    private fun updateRemainder() {
        items.forEach { item -> remainder += item.remainder }
    }

    fun getRemainder(): Int {
        return remainder;
    }

    fun add(item: Item) {
        items.add(item)
    }

    override fun toString(): String {
        return name
    }

    fun  hasFellow(fellow: Fellow): Boolean {
        return fellowsRelation.any{ it == fellow }
    }

    fun fellowsInSplittings(): LongArray {
        return items.flatMap{ it.fellowsInSplittings() }.toSet().toLongArray()
    }
}