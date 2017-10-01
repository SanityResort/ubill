package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

@Entity
class Item(val name: String = "", var sum: Int = 0,
           var splitEvenly: Boolean = true,
           @Transient private val splittingsParam: List<Splitting> = listOf()) {

    @Id
    var id: Long = 0
    lateinit var splittings: ToMany<Splitting>

    init {
        splittings.addAll(splittingsParam)
    }

    fun fellowsInSplittings(): List<Long> {
        return splittings.map { it.fellow.target.id }
    }

    fun updateSplittings(splittings: List<Splitting>) {
        this.splittings.clear()
        this.splittings.addAll(splittings)
    }
}