package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
class Splitting(@Transient private var fellowParam: Fellow? = null, val amount: Int = 0) {

    @Id
    var id: Long = 0

    lateinit var fellow: ToOne<Fellow>

    init {
        this.fellow.target = fellowParam
    }
}