package org.butterbrot.heve.ubill.entity;

import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class Item {

    @Id
    private Long id;
    private int sum;
    private List<Splitting> splittings;
    private int remainder;

    public Item(int sum, List<Splitting> splittings) {
        this.sum = sum;
        this.splittings = splittings;
        remainder = sum;

        for (Splitting splitting : splittings) {
            remainder -= splitting.getAmount();
        }

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public List<Splitting> getSplittings() {
        return splittings;
    }

    public void setSplittings(List<Splitting> splittings) {
        this.splittings = splittings;
    }

    public int getRemainder() {
        return remainder;
    }

    public void setRemainder(int remainder) {
        this.remainder = remainder;
    }
}