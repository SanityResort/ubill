package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id

@Entity
class Item (sum: Int, splittings: List<Splitting>) {

    @Id
    private var id: Long = 0

    var remainder: Int = sum

    init {
        val totalValue: Int = splittings.map { splitting -> splitting.amount }.reduceRight{acc, value -> acc + value}
        remainder -= totalValue
    }

    private constructor():this(0, listOf())

    fun getId() : Long {
        return id
    }

    fun setId(id: Long){
        this.id = id;
    }

}