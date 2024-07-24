package com.example.gestion_materielle_hackathon.model;
public class Material {
    private String name;
    private String quantity;
    private String description;

    // Default constructor
    public Material() {}

    public Material(String name, String quantity, String description) {
        this.name = name;
        this.quantity = quantity;
        this.description = description;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
