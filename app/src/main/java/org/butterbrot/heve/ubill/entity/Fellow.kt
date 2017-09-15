package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

@Entity
class Fellow(@Index val name: String = "") {

    @Id
    private var id: Long = 0

    fun getId() : Long {
        return id
    }

    fun setId(id: Long){
        this.id = id;
    }
}