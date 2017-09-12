package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class Fellow(val name: String = "") {

    @Id
    private var id: Long = 0

    fun getId() : Long {
        return id
    }

    fun setId(id: Long){
        this.id = id;
    }
}