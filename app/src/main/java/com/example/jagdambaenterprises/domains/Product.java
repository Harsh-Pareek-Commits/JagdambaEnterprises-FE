package com.example.jagdambaenterprises.domains;

import java.io.Serializable;
import java.sql.Blob;

public class Product  implements Serializable {
    private String id;
    private String name;
    private String brand;
    private Integer quantity;
    private Float costPrice;
    private Float sellingPrice;
    private Float wholesalePrice;
    private String tyreType;
    private String category;
    private String vehicleType;

    private String HSNCode;



    private User user;
    private Size size;
    private boolean selected;
    private Integer  requestedQunatity;

    public Integer getRequestedQunatity() {
        return requestedQunatity;
    }

    public void setRequestedQunatity(Integer requestedQunatity) {
        this.requestedQunatity = requestedQunatity;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private byte[] image;

    private boolean status;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    private boolean istubeless;

    public Product() {
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Float costPrice) {
        this.costPrice = costPrice;
    }

    public Float getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Float sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Float getWholesalePrice() {
        return wholesalePrice;
    }

    public void setWholesalePrice(Float wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public boolean isIstubeless() {
        return istubeless;
    }

    public void setIstubeless(boolean istubeless) {
        this.istubeless = istubeless;
    }

    public String getTyreType() {
        return tyreType;
    }

    public void setTyreType(String TyreType) {
        this.tyreType = TyreType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

     public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public String getHSNCode() {
        return HSNCode;
    }

    public void setHSNCode(String HSNCode) {
        this.HSNCode = HSNCode;
    }
}
