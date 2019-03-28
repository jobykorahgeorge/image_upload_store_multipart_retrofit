package com.prince.sirius_fr.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prince.sirius_fr.App;
import com.prince.sirius_fr.R;
import com.prince.sirius_fr.data.GetDataService;
import com.prince.sirius_fr.data.RetrofitClientInstance;
import com.prince.sirius_fr.models.ListImageResponse;
import com.prince.sirius_fr.models.ListImageTrainResponse;
import com.prince.sirius_fr.utilities.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SiriusImageViewActivity extends AppCompatActivity {

//    private SiriusImageViewAdapter siriusImageViewAdapter;
    private SessionManager mManager;
    public static TextView test;
    private ImageView settings;
    private TextView setIpText;
    private       Retrofit retrofit ;
//    public RecyclerView recyclerView;
    int flag = 1;
    public static ProgressDialog progressDialog;
    private  Boolean match;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/SuezOne-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        progressDialog = new ProgressDialog(this);

        setContentView(R.layout.activity_image_view);

        mManager = SessionManager.getInstance(this);

        mManager.setKeyIpAddressList(mManager.getKeyIpAddressList());

        test =(TextView) findViewById(R.id.test);
        settings = (ImageView) findViewById(R.id.black_setting);
        setIpText = (TextView) findViewById(R.id.set_ip_text);

        settings.setOnClickListener(v->{
            showIpConfig(false);
        });

        setData();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("test"));

//        test.setOnClickListener(v->{
//            Intent i = new Intent(this,SiriusImageViewActivity.class);
//            startActivity(i);
//        });



    }


    public void setData(){

        mManager = SessionManager.getInstance(this);
        mManager.setKeyIpAddressList(mManager.getKeyIpAddressList());


        String BASE_URL = SessionManager.getInstance(this).getKeyIpAddressList();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).addConverterFactory(ScalarsConverterFactory.create()).build();

        GetDataService service = retrofit.create(GetDataService.class);
        Call<ListImageResponse> call = service.getImageList();

        call.enqueue(new Callback<ListImageResponse>() {
            @Override
            public void onResponse(Call<ListImageResponse> call, Response<ListImageResponse> response) {
                if(response.isSuccessful()){
                    getImageViewAdapter(response.body());

                }
            }

            @Override
            public void onFailure(Call<ListImageResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"IP Adderss Unreachable",Toast.LENGTH_LONG);
            }
        });
    }


    public void showIpConfig(boolean isFirstTime){
        LayoutInflater li = LayoutInflater.from(this);
        View view = li.inflate(R.layout.ip_config_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(view);
        final EditText ipAddress = view.findViewById(R.id.edt_ip);
        final Button saveButton = view.findViewById(R.id.btn_save);

        if (isFirstTime) alertDialogBuilder.setCancelable(false);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Pattern pattern = Pattern.compile("^(http|https)://(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})?$");
        Pattern pattern1 = Pattern.compile("^(http|https)://(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})?$");
        Pattern pattern2 = Pattern.compile("^(http://|https://)(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$");
       // Pattern pattern2 = Pattern.compile("^(https?://)?(www\\\\.)?([-a-z0-9]{1,63}\\\\.)*?[a-z0-9][-a-z0-9]{0,61}[a-z0-9]\\\\.[a-z]{2,6}(/[-\\\\w@\\\\+\\\\.~#\\\\?&/=%]*)?$‌");

        if(!(mManager.getKeyIpAddressList()==null))
            ipAddress.setText(mManager.getKeyIpAddressList());


        saveButton.setOnClickListener(v -> {
            String ip = ipAddress.getText().toString();

            if (pattern.matcher(ip).matches()
                    || pattern1.matcher(ip).matches()
                    || pattern2.matcher(ip).matches()) {

                if(ip.equals("http://")
                        ||ip.contains(" ")
                        ||ip.equals("https://")){

                    ipAddress.setError("Enter Valid IP Address!");
                    Toast.makeText(getApplicationContext(), "Enter a valid ip", Toast.LENGTH_SHORT).show();
                }
                else{
                    mManager.setKeyIpAddressList(ip);
                    setIpText.setText(mManager.getKeyIpAddressList());
                    setData();
                    alertDialog.dismiss();

                    if (isFirstTime) onResume();
                    else ipAddress.setText(mManager.getKeyIpAddressList());
                }

            } else {
                ipAddress.setError("Enter Valid IP Address!");
                Toast.makeText(getApplicationContext(), "Enter a valid ip", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            Intent j = new Intent(this,SelectionActivity.class);
            startActivity(j);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            this.finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);


    }



    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            ArrayList<String> imageList = intent.getStringArrayListExtra("new");
            if (imageList.size() > 0) {
                Toast.makeText(SiriusImageViewActivity.this, imageList.get(0), Toast.LENGTH_SHORT).show();



                mManager = SessionManager.getInstance(SiriusImageViewActivity.this);
                mManager.setKeyIpAddressList(mManager.getKeyIpAddressList());

                //Remote data access

                        String BASE_URL = SessionManager.getInstance(SiriusImageViewActivity.this).getKeyIpAddressList();
                        Gson gson = new GsonBuilder()
                                .setLenient()
                                .create();
                        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).addConverterFactory(ScalarsConverterFactory.create()).build();

                        GetDataService service = retrofit.create(GetDataService.class);
                        Call<ListImageTrainResponse> call = service.sendImages(intent.getStringExtra("name"),imageList);

                        call.enqueue(new Callback<ListImageTrainResponse>() {
                            @Override
                            public void onResponse(Call<ListImageTrainResponse> call, Response<ListImageTrainResponse> response) {
                                Log.d("code:", Integer.toString(response.code()));
                                if (response.isSuccessful()) {
                                   /* imageList.clear();
                                    progressDialog.dismiss();
                                    setData();
                                    test.setVisibility(View.GONE);*/
                                    imageList.clear();
                                    progressDialog.dismiss();
                                    LayoutInflater li = LayoutInflater.from(SiriusImageViewActivity.this);
                                    View view = li.inflate(R.layout.popup_data_layout, null);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SiriusImageViewActivity.this);

                                    alertDialogBuilder.setView(view);
                                    final TextView textDataPopup = view.findViewById(R.id.textDataPopup);
                                    final TextView ok = view.findViewById(R.id.ok);
                                    alertDialogBuilder.setCancelable(true);

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();

                                    textDataPopup.setText("Successfully Trained!");
                                    ok.setOnClickListener(v -> {
                                        alertDialog.dismiss();

                                    });
                                    setData();
                                    test.setVisibility(View.GONE);


                                    /*Toast.makeText(SiriusImageViewActivity.this,"success",Toast.LENGTH_LONG);
                                    //Intent k = new Intent(SiriusImageViewActivity.this,SiriusImageViewActivity.class);
                                    //startActivity(k);*/
                                }
                                if(response.code()==400) {
                                    imageList.clear();
                                    progressDialog.dismiss();
                                    LayoutInflater li = LayoutInflater.from(SiriusImageViewActivity.this);
                                    View view = li.inflate(R.layout.popup_data_layout, null);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SiriusImageViewActivity.this);

                                    alertDialogBuilder.setView(view);
                                    final TextView textDataPopup = view.findViewById(R.id.textDataPopup);
                                    final TextView ok = view.findViewById(R.id.ok);
                                    alertDialogBuilder.setCancelable(true);

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();

                                    textDataPopup.setText("Parameter passing Error!");
                                    ok.setOnClickListener(v -> {
                                        alertDialog.dismiss();

                                    });
                                    setData();
                                    test.setVisibility(View.GONE);

                                }
                                if(response.code()==422){
                                    imageList.clear();
                                    progressDialog.dismiss();
                                    LayoutInflater li = LayoutInflater.from(SiriusImageViewActivity.this);
                                    View view = li.inflate(R.layout.popup_data_layout, null);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SiriusImageViewActivity.this);

                                    alertDialogBuilder.setView(view);
                                    final TextView textDataPopup = view.findViewById(R.id.textDataPopup);
                                    final TextView ok = view.findViewById(R.id.ok);
                                    alertDialogBuilder.setCancelable(true);

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();

                                    textDataPopup.setText("Training Failed!");
                                    ok.setOnClickListener(v -> {
                                        alertDialog.dismiss();
                                    });
                                    setData();
                                    test.setVisibility(View.GONE);


                                }
                                if(response.code()==500){
                                    imageList.clear();
                                    progressDialog.dismiss();
                                    LayoutInflater li = LayoutInflater.from(SiriusImageViewActivity.this);
                                    View view = li.inflate(R.layout.popup_data_layout, null);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SiriusImageViewActivity.this);

                                    alertDialogBuilder.setView(view);
                                    final TextView textDataPopup = view.findViewById(R.id.textDataPopup);
                                    final TextView ok = view.findViewById(R.id.ok);
                                    alertDialogBuilder.setCancelable(true);

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();

                                    textDataPopup.setText("Internal Server Error!");
                                    ok.setOnClickListener(v -> {
                                        alertDialog.dismiss();
                                    });
                                    setData();
                                    test.setVisibility(View.GONE);


                                }

                            }

                            @Override
                            public void onFailure(Call<ListImageTrainResponse> call, Throwable t) {
                                imageList.clear();
                                progressDialog.dismiss();
                                setData();
                                test.setVisibility(View.GONE);
                            }
                        });

                        imageList.clear();
                        //progressDialog.dismiss();
                        setData();
                        test.setVisibility(View.GONE);

            }
        }
    };


    private void getImageViewAdapter(ListImageResponse listImageResponses){
        RecyclerView recyclerView = findViewById(R.id.rv_Image);
        SiriusImageViewAdapter siriusImageViewAdapter = new SiriusImageViewAdapter(this,listImageResponses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(siriusImageViewAdapter);
        siriusImageViewAdapter.notifyDataSetChanged();

    }


    @Override
    protected void onResume() {
        super.onResume();
        flag = flag+1;
        if(flag == 2){
            if (mManager.getKeyIpAddressList() == null) {
                showIpConfig(true);
                return;
            } else {
                setIpText.setText(mManager.getKeyIpAddressList());
            }
        }

    }



}
