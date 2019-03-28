package com.prince.sirius_fr.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.prince.sirius_fr.R;
import com.prince.sirius_fr.utilities.SessionManager;

import java.util.regex.Pattern;

public class SelectionActivity extends AppCompatActivity {

    private RelativeLayout aadhaarLink;
    private RelativeLayout trainImages;
    private SessionManager mManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_activity);

        mManager = SessionManager.getInstance(this);

        aadhaarLink = (RelativeLayout) findViewById(R.id.link_aadhaar_rr);
        trainImages = (RelativeLayout) findViewById(R.id.train_images);

        aadhaarLink.setOnClickListener(v -> {
            Intent i = new Intent(this,HomeActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        trainImages.setOnClickListener(v -> {
            Intent i = new Intent(this,SiriusImageViewActivity.class);

            if((mManager.getKeyIpAddressList()==null)){
                showIpConfig(true);
            }else
            {
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                finish();
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

        saveButton.setOnClickListener(v -> {
            String ip = ipAddress.getText().toString();
            if (pattern.matcher(ip).matches()
                    ||pattern1.matcher(ip).matches()
                    ||pattern2.matcher(ip).matches()) {
                if(ip.equals("http://")
                        ||ip.contains(" ")
                        ||ip.equals("https://")){
                    ipAddress.setError("Enter Valid IP Address!");
                    Toast.makeText(getApplicationContext(), "Enter a valid ip", Toast.LENGTH_SHORT).show();
                }
                else{
                    mManager.setKeyIpAddressList(ip);
                    Intent j = new Intent(this,SiriusImageViewActivity.class);
                    startActivity(j);
                    alertDialog.dismiss();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Enter a valid ip", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            System.exit(0);
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
}
