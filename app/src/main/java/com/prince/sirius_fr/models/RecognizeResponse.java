package com.prince.sirius_fr.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecognizeResponse {

    @SerializedName("recognition")
    @Expose
    private String recognition;
    @SerializedName("uid")
    @Expose
    private String uid;

    public String getRecognition() {
        return recognition;
    }

    public void setRecognition(String recognition) {
        this.recognition = recognition;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}