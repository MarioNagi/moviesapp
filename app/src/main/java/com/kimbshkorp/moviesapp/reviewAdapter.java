package com.kimbshkorp.moviesapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.attr.resource;

/**
 * Created by Mario on 11/25/2016.
 */

public class reviewAdapter extends ArrayAdapter<review> {

    ArrayList<review> reviews;
    Context context;


    public reviewAdapter(Context context, ArrayList<review>reviews) {
        super(context, 0,reviews);
        this.reviews=reviews;
        this.context=context;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView=convertView;
        if (listView==null){
            listView= LayoutInflater.from(context).inflate(R.layout.review_listview,parent,false);
        }
        review currentReviewData=getItem(position);
        Log.v("www","review 1"+currentReviewData+"  "+position);
        TextView author_textView=(TextView)listView.findViewById(R.id.author_review);
        author_textView.setText(currentReviewData.getMreview_name());
        TextView conten_textView=(TextView)listView.findViewById(R.id.content_review);
        conten_textView.setText(currentReviewData.getMreview_content());

   return listView; }
}
