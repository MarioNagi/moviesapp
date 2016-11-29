package com.kimbshkorp.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.regex.Matcher;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.interfcallback{
boolean istwopane= false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
if (findViewById(R.id.fragment_details)!=null){istwopane=true;}

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_settings){
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void callOnclick(moviedata movie) {
        if (istwopane){

            Bundle bundle = new Bundle();
            bundle.putParcelable(Intent.EXTRA_TEXT, movie);

            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            detailActivityFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_details,detailActivityFragment).commit();
        }
        else {

            Intent intent = new Intent(this, DetailActivity.class).putExtra(Intent.EXTRA_TEXT, movie);
            startActivity(intent);
        }
    }
}
