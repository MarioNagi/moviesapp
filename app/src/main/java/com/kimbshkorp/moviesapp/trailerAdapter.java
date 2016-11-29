package com.kimbshkorp.moviesapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mario on 11/25/2016.
 */

public class trailerAdapter extends ArrayAdapter<String> {

    ArrayList<String> trailers;
    Context context;


    public trailerAdapter(Context context, ArrayList<String>trailers) {
        super(context, 0,trailers);
        this.trailers=trailers;
        this.context=context;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView=convertView;
        if (listView==null){
            listView= LayoutInflater.from(context).inflate(R.layout.trailers_listview,parent,false);
        }
        String currenttrailerData=getItem(position);
        ImageView trailer_image=(ImageView)listView.findViewById(R.id.trailer_image);
        Picasso.with(context).load("http://img.youtube.com/vi/"+currenttrailerData+"/2.jpg").into(trailer_image);

   return listView; }
}
