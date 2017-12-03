package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

@Entity
class Item(var name: String = "", var sum: Int = 0,
           var splitEvenly: Boolean = true,
           @Transient private val payerParam: Fellow? = null,
           @Transient private val splittingsParam: List<Splitting> = listOf()) {

    @Id
    var id: Long = 0
    lateinit var splittings: ToMany<Splitting>
    lateinit var payer: ToOne<Fellow>

    init {
        splittings.addAll(splittingsParam)
        payer.target = payerParam
    }

    fun fellowsInSplittings(): List<Long> {
        return splittings.map { it.fellow.target.id }
    }

    fun updateSplittings(splittings: List<Splitting>) {
        this.splittings.clear()
        this.splittings.addAll(splittings)
    }
}