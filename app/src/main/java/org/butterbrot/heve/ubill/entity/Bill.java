package org.butterbrot.heve.ubill.entity;

import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Bill {

    @Id
    private Long id;
    private List<Fellow> fellows;
    private List<Item> items;
    private int remainder;

    public Bill(List<Fellow> fellows, List<Item> items) {
        this.fellows = fellows;
        this.items = items;
        updateRemainder();
    }

    private void updateRemainder() {
        for (Item item : items) {
            remainder += item.getRemainder();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Fellow> getFellows() {
        return fellows;
    }

    public void setFellows(List<Fellow> fellows) {
        this.fellows = fellows;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getRemainder() {
        return remainder;
    }

    public void setRemainder(int remainder) {
        this.remainder = remainder;
    }
}