package com.kimbshkorp.moviesapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Mario on 10/21/2016.
 */

public class moviedata implements Parcelable {


    private String mId;
    private String mposter;
    private String mplot;
    private String mdate;
    private String mtitle;
    private String mrating;
    private ArrayList<review> mreview_array;
    private ArrayList<String> trialer_array;
    private String fav;

    public moviedata(String mId, String mposter, String mplot, String mdate, String mtitle, String mrating) {
        this.mId = mId;
        this.mposter = mposter;
        this.mplot = mplot;
        this.mdate = mdate;
        this.mtitle = mtitle;
        this.mrating = mrating;
        this.fav = "dislike";
        trialer_array = new ArrayList<>();
        mreview_array = new ArrayList<>();
    }

    public moviedata(String mId, String mposter, String mplot, String mdate, String mtitle, String mrating, String fav) {
        this.mId = mId;
        this.mposter = mposter;
        this.mplot = mplot;
        this.mdate = mdate;
        this.mtitle = mtitle;
        this.mrating = mrating;
        this.fav = fav;
        trialer_array = new ArrayList<>();
        mreview_array = new ArrayList<>();
    }

    protected moviedata(Parcel in) {
        mId = in.readString();
        mposter = in.readString();
        mplot = in.readString();
        mdate = in.readString();
        mtitle = in.readString();
        mrating = in.readString();
        mreview_array = in.createTypedArrayList(review.CREATOR);
        trialer_array = in.createStringArrayList();
        fav = in.readString();
    }

    public static final Creator<moviedata> CREATOR = new Creator<moviedata>() {
        @Override
        public moviedata createFromParcel(Parcel in) {
            return new moviedata(in);
        }

        @Override
        public moviedata[] newArray(int size) {
            return new moviedata[size];
        }
    };

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getMposter() {
        return mposter;
    }

    public void setMposter(String mposter) {
        this.mposter = mposter;
    }

    public String getMplot() {
        return mplot;
    }

    public void setMplot(String mplot) {
        this.mplot = mplot;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public String getMtitle() {
        return mtitle;
    }

    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }

    public String getMrating() {
        return mrating;
    }

    public void setMrating(String mrating) {
        this.mrating = mrating;
    }

    public ArrayList<review> getMreview_array() {
        return mreview_array;
    }

    public void setMreview_array(ArrayList<review> mreview_array) {
        this.mreview_array = mreview_array;
    }

    public ArrayList<String> getTrialer_array() {
        return trialer_array;
    }

    public void setTrialer_array(ArrayList<String> trialer_array) {
        this.trialer_array = trialer_array;
    }

    public String getFav() {
        return fav;
    }

    public void setFav(String fav) {
        this.fav = fav;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mposter);
        dest.writeString(mplot);
        dest.writeString(mdate);
        dest.writeString(mtitle);
        dest.writeString(mrating);
        dest.writeTypedList(mreview_array);
        dest.writeStringList(trialer_array);
        dest.writeString(fav);
    }
}