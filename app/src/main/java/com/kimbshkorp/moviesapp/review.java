package com.kimbshkorp.moviesapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Mario on 11/25/2016.
 */

public class review implements Parcelable {
    private String mreview_name;
    private String mreview_content;
    private String mreview_url;

    @Override
    public String toString() {
        return "review{" +
                "mreview_name='" + mreview_name + '\'' +
                ", mreview_content='" + mreview_content + '\'' +
                ", mreview_url='" + mreview_url + '\'' +
                '}';
    }

    public review(String review_name, String review_content, String review_url){

        mreview_content=review_content;
        mreview_name=review_name;
        mreview_url=review_url;
    }


    protected review(Parcel in) {
        mreview_name = in.readString();
        mreview_content = in.readString();
        mreview_url = in.readString();
    }

    public static final Creator<review> CREATOR = new Creator<review>() {
        @Override
        public review createFromParcel(Parcel in) {
            return new review(in);
        }

        @Override
        public review[] newArray(int size) {
            return new review[size];
        }
    };

    public String getMreview_name() {
        return mreview_name;
    }

    public void setMreview_name(String mreview_name) {
        this.mreview_name = mreview_name;
    }

    public String getMreview_content() {
        return mreview_content;
    }

    public void setMreview_content(String mreview_content) {
        this.mreview_content = mreview_content;
    }

    public String getMreview_url() {
        return mreview_url;
    }

    public void setMreview_url(String mreview_url) {
        this.mreview_url = mreview_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mreview_name);
        dest.writeString(mreview_content);
        dest.writeString(mreview_url);
    }
}

