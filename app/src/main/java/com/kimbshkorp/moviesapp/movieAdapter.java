package com.kimbshkorp.moviesapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mario on 10/21/2016.
 */

public class movieAdapter extends ArrayAdapter<moviedata> {
    ArrayList<moviedata>moviedatas   ;
    Context context;
    public movieAdapter(Context context, ArrayList<moviedata>moviedatas){
        super(context,0,moviedatas);
this.moviedatas=moviedatas;
        this.context=context;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridview=convertView;
if (gridview==null){
    gridview= LayoutInflater.from(context).inflate(R.layout.gridview_movies,parent,false);
}

        moviedata currentMovieData=getItem(position);
        ImageView poster=(ImageView) gridview.findViewById(R.id.grid_item_movie_image);
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/"+currentMovieData.getMposter()).into(poster);

        return gridview;

    }
}

