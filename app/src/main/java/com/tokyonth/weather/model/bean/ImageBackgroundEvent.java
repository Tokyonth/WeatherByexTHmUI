package com.tokyonth.weather.model.bean;

public class ImageBackgroundEvent {

    private boolean isImageBg;
    private String imagePath;

    public ImageBackgroundEvent(boolean isImageBg, String imagePath) {
        this.isImageBg = isImageBg;
        this.imagePath = imagePath;
    }

    public void setImageBg(boolean imageBg) {
        isImageBg = imageBg;
    }

    public boolean isImageBg() {
        return isImageBg;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

}
