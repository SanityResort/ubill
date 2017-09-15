package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index

@Entity
class Bill(@Index val name: String = "", val fellows: List<Fellow> = listOf(), val items: MutableList<Item> = mutableListOf()) {

    @Id
    private var id: Long = 0
    private var remainder: Int = 0

    init {
        updateRemainder();
    }

    private fun updateRemainder() {
        items.forEach { item -> remainder += item.remainder }
    }

    fun getId() : Long {
        return id
    }

    fun setId(id: Long){
        this.id = id;
    }

    fun getRemainder(): Int {
        return remainder;
    }

    fun add(item: Item) {
        items.add(item)
    }
}