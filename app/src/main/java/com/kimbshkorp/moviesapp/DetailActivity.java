package com.kimbshkorp.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import static android.content.Intent.EXTRA_TEXT;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            moviedata movie = getIntent().getExtras().getParcelable(EXTRA_TEXT);
            Log.d("www","inside mn "+movie.getMtitle());

            Bundle bundle = new Bundle();
            bundle.putParcelable(Intent.EXTRA_TEXT, movie);

            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            detailActivityFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_details, detailActivityFragment).commit();
        }

    }



    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {

        return super.getSupportParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
