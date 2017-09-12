package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id

@Entity
class Item (sum: Int = 0, splittings: List<Splitting> = listOf()) {

    @Id
    private var id: Long = 0

    var remainder: Int = sum

    init {
        val totalValue: Int = splittings.map { splitting -> splitting.amount }.reduceRight{acc, value -> acc + value}
        remainder -= totalValue
    }

    fun getId() : Long {
        return id
    }

    fun setId(id: Long){
        this.id = id;
    }

}