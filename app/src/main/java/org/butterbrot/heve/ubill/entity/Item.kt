package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

@Entity
class Item(val name: String = "", val sum: Int = 0, @Transient private val splittingsParam: List<Splitting> = listOf()) {

    @Id
    var id: Long = 0
    lateinit var splittings: ToMany<Splitting>

    init {
        splittings.addAll(splittingsParam)
    }

    fun fellowsInSplittings(): List<Long> {
        return splittings.map { it.fellow.target.id }
    }
}