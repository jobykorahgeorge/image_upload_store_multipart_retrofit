package com.prince.sirius_fr.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListImageResponse {

    @SerializedName("images")
    @Expose
    private List<String> images = null;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

}