package com.tokyonth.weather.model.bean;

public class BlurEvent {

    private boolean isBlur;
    private int radius;
    private int scaleFactor;
    private int roundCorner;

    public BlurEvent(boolean isBlur, int radius, int scaleFactor, int roundCorner) {
        this.isBlur = isBlur;
        this.radius = radius;
        this.scaleFactor = scaleFactor;
        this.roundCorner = roundCorner;
    }

    public boolean isBlur() {
        return isBlur;
    }

    public void setBlur(boolean blur) {
        isBlur = blur;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(int scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public int getRoundCorner() {
        return roundCorner;
    }

    public void setRoundCorner(int roundCorner) {
        this.roundCorner = roundCorner;
    }

}
