package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

@Entity
class Fellow(@Index val name: String = "") {

    @Id
    var id: Long = 0

}