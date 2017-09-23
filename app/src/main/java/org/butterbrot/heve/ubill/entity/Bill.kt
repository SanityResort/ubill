package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index
import io.objectbox.relation.ToMany

@Entity
class Bill(@Index var name: String = "", @Transient private val fellowsParam: List<Fellow> = listOf(),
           @Transient private val itemsParam: List<Item> = listOf()) {

    @Id
    var id: Long = 0
    lateinit var fellows: ToMany<Fellow>
    lateinit var items: ToMany<Item>

    init {
        fellows.addAll(fellowsParam)
        items.addAll(itemsParam)
    }

    fun setFellows(fellows: List<Fellow>) {
        this.fellows.clear()
        this.fellows.addAll(fellows)
    }

    fun add(item: Item) {
        items.add(item)
    }

    override fun toString(): String {
        return name
    }

    fun  hasFellow(fellow: Fellow): Boolean {
        return fellows.any{ it == fellow }
    }

    fun fellowsInSplittings(): LongArray {
        return items.flatMap{ it.fellowsInSplittings() }.toSet().toLongArray()
    }
}