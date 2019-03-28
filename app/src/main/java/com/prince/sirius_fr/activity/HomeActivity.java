package com.prince.sirius_fr.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.prince.sirius_fr.R;
import com.prince.sirius_fr.data.GetDataService;
import com.prince.sirius_fr.data.RetrofitClientInstance;
import com.prince.sirius_fr.models.RecognizeResponse;
import com.prince.sirius_fr.models.TrainingResponse;
import com.prince.sirius_fr.utilities.SessionManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity {

    private static final int MY_CAMERA_REQUEST_CODE = 400;
    private final int REQUEST_EXTERNAL_PERMISSION = 100;
    private final int TAKE_PHOTO_CODE_TRAIN = 20;
    private final int TAKE_PHOTO_CODE_MATCH = 30;
    int flag = 1;
    File image;//training image
    File image2;//match found

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    RelativeLayout qrScan,qrData,imageTrain,trainedGoodMsg,imageDocument,documentGoodMsg,trainLayout,documentLayout;
    IntentIntegrator integrator;
    String dataSet;
    SessionManager mManager;
    TextView uid, name,setIpText;
    ImageView white_setting,qrChecked,trainingChecked,documentChecked,newUser;
    CircleImageView trainedImage,documentImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mManager = SessionManager.getInstance(this);
        integrator = new IntentIntegrator(this);


        //PERMISSION FOR VERSION 6.0+(API Level 23)
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_PERMISSION);

        }

        //Binding All view elements to activity
        initializeBinding();

        //Lambda listner for scan QR
        qrScan.setOnClickListener(v -> {
            scanNow();
        });

        //Lambda listner for setting IP Address
        setIpText.setOnClickListener(v -> {
            showIpConfigDialog(false);
        });

        //Lambda listner for setting IP Address
        white_setting.setOnClickListener(v -> {
            showIpConfigDialog(false);
        });

        //Clear Session
        newUser.setOnClickListener(v -> {
            newRegistration();
        });

        //Train Image Request
        imageTrain.setOnClickListener(v -> {
            trainImage();
        });

        //Match Image Request
        imageDocument.setOnClickListener(v -> {
            matchImage();
        });




    }

    public void matchImage(){
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "FRData");
        imagesFolder.mkdirs();

        image2 = new File(imagesFolder,   mManager.getAadhaarId()+"11"+ ".jpg");
        Uri uriSavedImage = Uri.fromFile(image2);

        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, TAKE_PHOTO_CODE_MATCH);
    }

    public void trainImage(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_PERMISSION);
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "FRData");
        imagesFolder.mkdirs();

        image = new File(imagesFolder,   mManager.getAadhaarId() + ".jpg");
        Uri uriSavedImage = Uri.fromFile(image);

        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, TAKE_PHOTO_CODE_TRAIN);
    }

    public void newRegistration(){
        qrData.setVisibility(View.GONE);
        qrChecked.setVisibility(View.GONE);
        trainLayout.setVisibility(View.GONE);
        documentLayout.setVisibility(View.GONE);
        trainedImage.setVisibility(View.GONE);
        trainingChecked.setVisibility(View.GONE);
        trainedGoodMsg.setVisibility(View.GONE);
        documentImage.setVisibility(View.GONE);
        documentGoodMsg.setVisibility(View.GONE);
        documentChecked.setVisibility(View.GONE);
    }

    public void initializeBinding(){

        qrScan = (RelativeLayout) findViewById(R.id.rl_images);
        qrData = (RelativeLayout) findViewById(R.id.qr_data);
        imageTrain = (RelativeLayout) findViewById(R.id.rl_images_user);
        trainedGoodMsg = (RelativeLayout) findViewById(R.id.traind_good_msg);
        trainLayout = (RelativeLayout) findViewById(R.id.train_layout);
        documentLayout = (RelativeLayout) findViewById(R.id.document_layot);
        imageDocument = (RelativeLayout) findViewById(R.id.rl_images_user1);
        documentGoodMsg = (RelativeLayout) findViewById(R.id.document_message);

        uid = (TextView) findViewById(R.id.uid);
        name = (TextView) findViewById(R.id.name);
        setIpText = (TextView) findViewById(R.id.set_ip_text);

        white_setting = (ImageView) findViewById(R.id.white_setting);
        qrChecked =(ImageView) findViewById(R.id.qr_checked);
        trainingChecked = (ImageView) findViewById(R.id.training_checked);
        documentChecked = (ImageView) findViewById(R.id.document_checked);
        newUser = (ImageView) findViewById(R.id.newUser);
        trainedImage = (CircleImageView) findViewById(R.id.trained_image) ;
        documentImage = (CircleImageView) findViewById(R.id.trained_image1);

        trainLayout.setVisibility(View.GONE);
        documentLayout.setVisibility(View.GONE);
        qrChecked.setVisibility(View.GONE);
        trainingChecked.setVisibility(View.GONE);
        documentChecked.setVisibility(View.GONE);
        trainedGoodMsg.setVisibility(View.GONE);
        trainedImage.setVisibility(View.GONE);
    }

    public void scanNow() {
        // we need to check if the user has granted the camera permissions
        // otherwise scanner will not work
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            return;
        }

        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan a Aadharcard QR Code");
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true);
        integrator.setCameraId(0);// Use a specific camera of the device
        integrator.initiateScan();

    }

    private void showIpConfigDialog(boolean isFirstTime) {
        LayoutInflater li = LayoutInflater.from(this);
        View view = li.inflate(R.layout.ip_config_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(view);
        final EditText ipAddress = view.findViewById(R.id.edt_ip);
        final Button saveButton = view.findViewById(R.id.btn_save);

        if (isFirstTime) alertDialogBuilder.setCancelable(false);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        if(!(mManager.getKeyIpAddress()==null))
            ipAddress.setText(mManager.getKeyIpAddress());


        Pattern pattern = Pattern.compile("^(http|https)://(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})?$");
        Pattern pattern1 = Pattern.compile("^(http|https)://(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})?$");
        Pattern pattern2 = Pattern.compile("^(http://|https://)(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$");
//        Pattern pattern3 = Pattern.compile("https://(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");

        saveButton.setOnClickListener(v -> {
            String ip = ipAddress.getText().toString();
            if (pattern.matcher(ip).matches()
                    ||pattern1.matcher(ip).matches()
                    ||pattern2.matcher(ip).matches()) {

                if(ip.equals("http://")||
                        ip.contains(" ")||
                        ip.equals("https://")){
                    ipAddress.setError("Enter Valid IP Address!");
                    Toast.makeText(getApplicationContext(), "Enter a valid ip", Toast.LENGTH_SHORT).show();
                }
                else{

                    mManager.setKeyIpAddress(ip);
                    setIpText.setText(mManager.getKeyIpAddress());
                    alertDialog.dismiss();

                    if (isFirstTime) onResume();
                    else ipAddress.setText(mManager.getKeyIpAddress());
                }

            } else {
                ipAddress.setError("Enter Valid IP Address!");
                Toast.makeText(getApplicationContext(), "Enter a valid ip", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result

        //TRAINING IMAGE RESULT
        if(requestCode==TAKE_PHOTO_CODE_TRAIN && resultCode==RESULT_OK){
            final ProgressDialog pDialog = new ProgressDialog(this);

            pDialog.setMessage(getString(R.string.uploading));
            pDialog.setCancelable(false);
            pDialog.show();

            Thread mThread = new Thread(){
                @Override
                public void run() {
                    File file1 = new File(image.getAbsolutePath());

                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file1);

                    final RequestBody requestBody1= RequestBody.create(MediaType.parse("text/plain"),mManager.getUserName());
                    final RequestBody requestBody2= RequestBody.create(MediaType.parse("text/plain"),mManager.getAadhaarId());

                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file1.getName(), requestFile);
                    GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

                    Call<TrainingResponse> call = service.addToSubject(body,requestBody1,requestBody2);

                    call.enqueue(new Callback<TrainingResponse>() {
                        @Override
                        public void onResponse(Call<TrainingResponse> call, Response<TrainingResponse> response) {
                            if (response.isSuccessful()) {
                                TrainingResponse trainingResponse = response.body();
                                Toast.makeText(HomeActivity.this, trainingResponse.getTraining(), Toast.LENGTH_LONG).show();
                                pDialog.dismiss();

                                  setTrainSuccessLayout(image.getAbsolutePath());


                            }
                            else {
                                pDialog.dismiss();
                                Toast.makeText(HomeActivity.this, Integer.toString(response.code())+" No Faces recognized: \n Capture Again !" , Toast.LENGTH_SHORT).show();

                                Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "FRData");
                                imagesFolder.mkdirs();

                                image = new File(imagesFolder,   mManager.getAadhaarId() + ".jpg");
                                Uri uriSavedImage = Uri.fromFile(image);

                                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                                startActivityForResult(imageIntent, TAKE_PHOTO_CODE_TRAIN);
                            }
                        }

                        @Override
                        public void onFailure(Call<TrainingResponse> call, Throwable t) {
                            t.printStackTrace();
                            pDialog.dismiss();
                            Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "FRData");
                            imagesFolder.mkdirs();

                            image = new File(imagesFolder,   mManager.getAadhaarId() + ".jpg");
                            Uri uriSavedImage = Uri.fromFile(image);

                            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                            startActivityForResult(imageIntent, TAKE_PHOTO_CODE_TRAIN);
                        }
                    });

                }
            };
            mThread.start();
        }

        //MATCH IMAGE RESULT
        if(requestCode==TAKE_PHOTO_CODE_MATCH && resultCode==RESULT_OK){

            final ProgressDialog pDialog = new ProgressDialog(this);

            pDialog.setMessage(getString(R.string.finding));
            pDialog.setCancelable(false);
            pDialog.show();
            mManager = SessionManager.getInstance(this);

            Thread mThread = new Thread(){
                @Override
                public void run() {
                    File file1 = new File(image2.getAbsolutePath());

                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file1);

                    final RequestBody requestBody2= RequestBody.create(MediaType.parse("text/plain"),mManager.getAadhaarId());

                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file1.getName(), requestFile);
                    GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

                    Call<RecognizeResponse> call = service.addToRecognize(body,requestBody2);

                    call.enqueue(new Callback<RecognizeResponse>() {
                        @Override
                        public void onResponse(Call<RecognizeResponse> call, Response<RecognizeResponse> response) {
                            if (response.isSuccessful()) {
                                RecognizeResponse recognizeResponse = response.body();
                                Toast.makeText(HomeActivity.this, recognizeResponse.getRecognition(), Toast.LENGTH_LONG).show();
                                pDialog.dismiss();
                                setMatchSuccessLayout(image2.getAbsolutePath());
                            }
                            else {
                                pDialog.dismiss();
                                Toast.makeText(HomeActivity.this, Integer.toString(response.code())+" No Matches recognized: \n Capture Another Doc !" , Toast.LENGTH_SHORT).show();

                                Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "FRData");
                                imagesFolder.mkdirs();

                                image = new File(imagesFolder,   mManager.getAadhaarId() + ".jpg");
                                Uri uriSavedImage = Uri.fromFile(image);

                                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                                startActivityForResult(imageIntent, TAKE_PHOTO_CODE_MATCH);

                            }
                        }

                        @Override
                        public void onFailure(Call<RecognizeResponse> call, Throwable t) {
                            t.printStackTrace();
                            Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "FRData");
                            imagesFolder.mkdirs();

                            image = new File(imagesFolder,   mManager.getAadhaarId() + ".jpg");
                            Uri uriSavedImage = Uri.fromFile(image);

                            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                            startActivityForResult(imageIntent, TAKE_PHOTO_CODE_MATCH);
                        }
                    });

                }
            };
            mThread.start();
        }

        //QR CODE SCANNING
        if(requestCode==49374){


            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

            if (scanningResult != null) {

                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();

                if (scanContent != null && !scanContent.isEmpty()) {
                    processScannedData(scanContent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Scan Cancelled", Toast.LENGTH_SHORT);
                    toast.show();
                }

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }

    public void setMatchSuccessLayout(String matchImagePath){
        qrData.setVisibility(View.VISIBLE);
        qrChecked.setVisibility(View.VISIBLE);
        uid.setText(mManager.getAadhaarId());
        name.setText(mManager.getUserName());
        trainLayout.setVisibility(View.VISIBLE);
        trainingChecked.setVisibility(View.VISIBLE);

        documentImage.setVisibility(View.VISIBLE);

        Bitmap myBitmap1 = BitmapFactory.decodeFile(matchImagePath);
       // Bitmap myResizedBitmap1 = myBitmap1.createScaledBitmap(myBitmap1,70,70,true);
        documentImage.setImageBitmap(scaleCenterCrop(myBitmap1,100,100));

        documentLayout.setVisibility(View.VISIBLE);
        documentChecked.setVisibility(View.VISIBLE);
        trainedGoodMsg.setVisibility(View.VISIBLE);
        documentGoodMsg.setVisibility(View.VISIBLE);
    }

    public void setTrainSuccessLayout(String trainImagePath){
        qrData.setVisibility(View.VISIBLE);
        qrChecked.setVisibility(View.VISIBLE);
        uid.setText(mManager.getAadhaarId());
        name.setText(mManager.getUserName());
        trainLayout.setVisibility(View.VISIBLE);
        trainingChecked.setVisibility(View.VISIBLE);
        trainedImage.setVisibility(View.VISIBLE);

        Bitmap myBitmap = BitmapFactory.decodeFile(trainImagePath);


        //Bitmap myResizeBitmap3 = myBitmap.createScaledBitmap(myBitmap,100,100,true);

        trainedImage.setImageBitmap(scaleCenterCrop(myBitmap,100,100));
        documentLayout.setVisibility(View.VISIBLE);
        documentChecked.setVisibility(View.GONE);
        documentImage.setVisibility(View.INVISIBLE);
        documentGoodMsg.setVisibility(View.GONE);
        trainedGoodMsg.setVisibility(View.VISIBLE);
    }


    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    protected void processScannedData(String scanData) {
        Log.d("Rajdeol", scanData);

        dataSet = scanData.replace("?\"", "\"?");

        Pattern number = Pattern.compile("uid=\"([^\"]*)\"");
        Pattern Aname = Pattern.compile("name=\"([^\"]*)\"");
        Pattern Agender = Pattern.compile("gender=\"([^\"]*)\"");
        Matcher num = number.matcher(dataSet);
        Matcher nme = Aname.matcher(dataSet);
        Matcher gndr = Agender.matcher(dataSet);
        num.find();
        nme.find();
        gndr.find();


        if (!num.toString().contains("lastmatch=]") || !nme.toString().contains("lastmatch=]")) {
            mManager.setAadhaarId(num.group(1));
            mManager.setUserName(nme.group(1));

            uid.setText("UID : " + mManager.getAadhaarId());
            name.setText("Name : " + mManager.getUserName());

            mManager.setAadhaarId(num.group(1));
            mManager.setUserName(nme.group(1));

            qrData.setVisibility(View.VISIBLE);
            trainLayout.setVisibility(View.VISIBLE);
            qrChecked.setVisibility(View.VISIBLE);

        } else {
            qrData.setVisibility(View.GONE);
            qrChecked.setVisibility(View.GONE);
            trainLayout.setVisibility(View.GONE);
            Toast.makeText(this, "NOT AN AADHAAR!", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(HomeActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
            flag = flag+1;
            if(flag == 2){
                if (mManager.getKeyIpAddress() == null) {
                    showIpConfigDialog(true);
                    return;
                } else {
                    setIpText.setText(mManager.getKeyIpAddress());
                }
            }


    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            Intent j = new Intent(this,SelectionActivity.class);
            startActivity(j);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
}
