package org.example.model;

public class PaymentMethod {
    private long Id;
    private String name;
    private String description;

    public PaymentMethod(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }
}
