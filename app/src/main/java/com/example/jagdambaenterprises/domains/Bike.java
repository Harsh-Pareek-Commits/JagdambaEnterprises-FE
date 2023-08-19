package com.example.jagdambaenterprises.domains;

public class Bike {
    private long id;
    private String manufacturer;
    private String model;
    private Size rearTyreSize;
    private Size frontTyreSize;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Size getRearTyreSize() {
        return rearTyreSize;
    }

    public void setRearTyreSize(Size rearTyreSize) {
        this.rearTyreSize = rearTyreSize;
    }

    public Size getFrontTyreSize() {
        return frontTyreSize;
    }

    public void setFrontTyreSize(Size frontTyreSize) {
        this.frontTyreSize = frontTyreSize;
    }
}