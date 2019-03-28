package com.prince.sirius_fr.data;

import com.prince.sirius_fr.models.ListImageResponse;
import com.prince.sirius_fr.models.ListImageTrainResponse;
import com.prince.sirius_fr.models.RecognizeResponse;
import com.prince.sirius_fr.models.TrainingResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface GetDataService {

    //PROFILE APIS

    //    @FormUrlEncoded


    //Service call to train Image
    @Multipart
    @POST("/aadhaar/api/v1.0/train")
    Call<TrainingResponse> addToSubject(
            @Part MultipartBody.Part file,
            @Part("name") RequestBody name,
            @Part("uid") RequestBody uid);


    //Service call to recognize Image
    @Multipart
    @POST("/aadhaar/api/v1.0/recognize")
    Call<RecognizeResponse> addToRecognize(
            @Part MultipartBody.Part file,
            @Part("uid") RequestBody uid);

    //Service call to list all Images from remote folder
    @GET("/aadhaar/api/v1.0/images")
    Call <ListImageResponse> getImageList(

    );

    //Service call to train selected Images with name
    @FormUrlEncoded
    @POST("/aadhaar/api/v1.0/tag")
    Call <ListImageTrainResponse> sendImages(
            @Field("tag") String tag,
            @Field("image_list[]") ArrayList<String> images
    );



//    @GET("/my-transactions")
//    Call<List<TransactionModel>> getTransaction(
//            @Query("email") String email,
//            @Query("vehicle_no") String vehicleNo
//    );
//
//    @PUT("/add-money")
//    Call<String> updateMoney(
//            @Query("email") String email,
//            @Query("vehicle_no") String vehicleNo,
//            @Query("money") String money
//    );


}
