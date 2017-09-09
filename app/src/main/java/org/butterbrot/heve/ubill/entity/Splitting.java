package org.butterbrot.heve.ubill.entity;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class Splitting {

    @Id
    private Long id;
    ToOne<Fellow> fellow;
    private int amount;

    public Splitting(Fellow fellow, int amount) {
        this.fellow.setTarget(fellow);
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ToOne<Fellow> getFellow() {
        return fellow;
    }

    public void setFellow(ToOne<Fellow> fellow) {
        this.fellow = fellow;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}