package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id

@Entity
class Item (val name: String, val sum: Int = 0, val splittings: List<Splitting> = listOf()) {

    @Id
    var id: Long = 0

    var remainder: Int = sum

    init {
        val totalValue: Int = splittings.map { splitting -> splitting.amount }.reduceRight{acc, value -> acc + value}
        remainder -= totalValue
    }

    fun fellowsInSplittings(): List<Long> {
        return splittings.map { it.fellowRelation.target.id  }
    }
}