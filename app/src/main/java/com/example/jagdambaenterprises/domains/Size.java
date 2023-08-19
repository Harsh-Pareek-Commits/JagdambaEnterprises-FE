package com.example.jagdambaenterprises.domains;

public class Size extends Base{
    private int aspectRatio; // Aspect ratio of the tire
    private float width;       // Width of the tire in millimeters
    private int rimDiameter;

    public int getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(int aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public int getRimDiameter() {
        return rimDiameter;
    }

    public void setRimDiameter(int rimDiameter) {
        this.rimDiameter = rimDiameter;
    }
}
