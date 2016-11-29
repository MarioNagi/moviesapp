package com.kimbshkorp.moviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mario on 11/28/2016.
 */

public class moviedatabase_heleper extends SQLiteOpenHelper {


    public moviedatabase_heleper(Context context) {
        super(context, "movies.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("Create Table movie ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "              movie_id TEXT UNIQUE NOT NULL," +
                "              movie_poster TEXT NOT NULL," +
                "              movie_plot TEXT NOT NULL," +
                "              movie_date TEXT NOT NULL," +
                "              movie_title TEXT NOT NULL," +
                "              movie_rating TEXT NOT NULL," +
                "              movie_fav TEXT NOT NULL DEFAULT 'dislike');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" drop from movie  ");
        onCreate(db);
    }
}
