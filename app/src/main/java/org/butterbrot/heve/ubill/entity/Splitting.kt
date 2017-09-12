package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
class Splitting(@Transient var fellow: Fellow? = null, val amount: Int = 0) {

    @Id
    private var id: Long = 0

    lateinit var fellowRelation: ToOne<Fellow>

    fun updateFellow(fellow: Fellow) {
        this.fellow = fellow
        fellowRelation.target = fellow
    }

    fun getId() : Long {
        return id
    }

    fun setId(id: Long){
        this.id = id;
    }

}