package com.example.jagdambaenterprises.domains;

import com.example.jagdambaenterprises.constants.SizeUnit;

import java.io.Serializable;

public class Size extends Base implements Serializable {
    private String aspectRatio; // Aspect ratio of the Tyre

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    private float width;       // Width of the Tyre in millimeters
    private int rim;
    private SizeUnit sizeUnit;

    private int productSize;

    public SizeUnit getSizeUnit() {
        return sizeUnit;
    }

    public void setSizeUnit(SizeUnit sizeUnit) {
        this.sizeUnit = sizeUnit;
    }

    public int getProductSize() {
        return productSize;
    }

    public void setProductSize(int productSize) {
        this.productSize = productSize;
    }

    public String getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(String aspectRatio) {
        this.aspectRatio = aspectRatio;
    }


    public int getRim() {
        return rim;
    }

    public void setRim(int rim) {
        this.rim = rim;
    }
}
