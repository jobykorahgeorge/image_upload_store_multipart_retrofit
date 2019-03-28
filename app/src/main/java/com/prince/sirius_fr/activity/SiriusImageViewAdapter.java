package com.prince.sirius_fr.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prince.sirius_fr.R;
import com.prince.sirius_fr.models.ListImageResponse;
import com.prince.sirius_fr.utilities.CircleTransform;
import com.prince.sirius_fr.utilities.SessionManager;
import com.squareup.picasso.Picasso;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static com.prince.sirius_fr.activity.SiriusImageViewActivity.progressDialog;

public class SiriusImageViewAdapter extends RecyclerView.Adapter <SiriusImageViewAdapter.ImageViewHolder> {

    private Context context;
    private ListImageResponse listImageResponses;
    private SessionManager mManager;
    List<String> images;


    private String subString;
    private int selectedPosition = -1;


    SiriusImageViewAdapter(Context context,ListImageResponse listImageResponses){
        this.context = context;
        this.listImageResponses = listImageResponses;
        this.images = new ArrayList<>();
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.imave_view_recycler_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SiriusImageViewAdapter.ImageViewHolder holder, int position) {


        subString = listImageResponses.getImages().get(position);
        mManager = SessionManager.getInstance(context);
        holder.imageName.setText(subString.replace("static/images/",""));

        Picasso.with(context)
                .load(mManager.getKeyIpAddressList()+"/"+(listImageResponses.getImages()
                .get(position))
                .replaceAll(" ","%20"))
                .placeholder(R.drawable.progress_anim)
                .transform(new CircleTransform())
                .centerCrop()
                .resize(200,200)
                .into(holder.imageView);


        holder.checkBox.setOnCheckedChangeListener(null);


        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    holder.checkBox.setSelected(true);
                    holder.itemView.setBackgroundColor(Color.parseColor("#a3a5d4ee"));
                    images.add(mManager.getKeyIpAddress()+"/"+listImageResponses.getImages().get(position));


                     if(images.size()>0)
                         SiriusImageViewActivity.test.setVisibility(View.VISIBLE);
                     else
                         SiriusImageViewActivity.test.setVisibility(View.GONE);
                }
                else{
                    holder.checkBox.setSelected(false);
                    holder.itemView.setBackgroundColor(Color.parseColor("#00a5d4ee"));
                    images.remove(mManager.getKeyIpAddress()+"/"+listImageResponses.getImages().get(position));
                    if(images.size()>0)
                        SiriusImageViewActivity.test.setVisibility(View.VISIBLE);
                    else
                        SiriusImageViewActivity.test.setVisibility(View.GONE);
                }
            }
        });


        SiriusImageViewActivity.test.setOnClickListener(v -> {

            LayoutInflater li = LayoutInflater.from(v.getRootView().getContext());
            View view = li.inflate(R.layout.image_list_name, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getRootView().getContext());

            alertDialogBuilder.setView(view);
            final EditText name = view.findViewById(R.id.name);
            final TextView saveButton = view.findViewById(R.id.save);
            final TextView cancel = view.findViewById(R.id.cancel);
            final TextView count = view.findViewById(R.id.count_image_selected);

            count.setText(images.size() +" : images selected");

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setIcon(R.drawable.image_bg);
            alertDialog.setCancelable(false);

            saveButton.setOnClickListener(v1 -> {
                if(name.getText().toString().equals("") || name.getText().toString().equals(" ")){
                    name.setError("Enter valid name!");
                }
                else {
                    Intent j = new Intent("test");
                    j.putStringArrayListExtra("new", (ArrayList<String>) images);
                    j.putExtra("name", name.getText().toString());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(j);
                    alertDialog.dismiss();


                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Please wait");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });

            cancel.setOnClickListener(v2->{
                alertDialog.dismiss();
            });

        });

    }


    @Override
    public int getItemViewType(int position) {

        return position;
    }



    @Override
    public int getItemCount() {
        return listImageResponses.getImages().size();
    }


    class ImageViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        TextView imageName;
        ImageView imageView;
        CheckBox checkBox;

        public ImageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            imageName = (TextView) mView.findViewById(R.id.image_name);
            imageView = (ImageView) mView.findViewById(R.id.image_view);
            checkBox = (CheckBox) mView.findViewById(R.id.check_box);

        }
    }




}
