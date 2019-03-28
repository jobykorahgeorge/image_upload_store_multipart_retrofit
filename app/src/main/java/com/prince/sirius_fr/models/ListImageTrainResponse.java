package com.prince.sirius_fr.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListImageTrainResponse {

    @SerializedName("training")
    @Expose
    private String training;

    public String getTraining() {
        return training;
    }

    public void setTraining(String training) {
        this.training = training;
    }

}