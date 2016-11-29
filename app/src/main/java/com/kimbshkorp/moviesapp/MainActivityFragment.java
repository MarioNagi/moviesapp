package com.kimbshkorp.moviesapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.kimbshkorp.moviesapp.data.moviedatabase_heleper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivityFragment extends Fragment {
    public ArrayList<moviedata> movieee = new ArrayList<moviedata>();

    public movieAdapter getmMoviesAdapter() {
        return mMoviesAdapter;
    }

    public void setmMoviesAdapter(movieAdapter mMoviesAdapter) {
        this.mMoviesAdapter = mMoviesAdapter;
    }

    private movieAdapter mMoviesAdapter;
    interfcallback interfcallbac;
    public MainActivityFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        interfcallbac = (interfcallback) activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        interfcallbac = (interfcallback) context;
    }

    public void updatemovies() {

        fetchMoviesTask moviesTask = new fetchMoviesTask();
        moviesTask.execute();
    }





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        CheckNetwork checkNetwork=new CheckNetwork(getActivity());
        if(checkNetwork.isNetworkAvailable()){
            //testing with toast
            //Toast.makeText(getActivity(),"everything ok",Toast.LENGTH_SHORT).show();
            updatemovies();
        }
else { Toast.makeText(getActivity(),"No internet Connection,Retrieving from the database",Toast.LENGTH_SHORT).show();
            moviedatabase_heleper heleper = new moviedatabase_heleper(getActivity());
            SQLiteDatabase sqLiteDatabase = heleper.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.query("movie",null,null,null,null,null,null);
            mMoviesAdapter.clear();

            mMoviesAdapter.addAll(getarraylistfromcursor(cursor));

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


    mMoviesAdapter = new movieAdapter(getActivity(), new ArrayList<moviedata>());
    GridView gridView = (GridView) rootView.findViewById(R.id.gridView_movies);
    gridView.setAdapter(mMoviesAdapter);
    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            moviedata movie = mMoviesAdapter.getItem(position);
            interfcallbac.callOnclick(movie);
//            Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, movie);
//            startActivity(intent);
        }
    });



        return rootView;

    }


    private ArrayList<moviedata> getmovieDataFromJson(String moviesJsonStr) throws Exception {
        final String results = "results";
        final String movie_id = "id";
        final String movie_poster = "poster_path";
        final String movie_plot = "overview";
        final String movie_date = "release_date";
        final String movie_title = "original_title";
        final String movie_rating = "vote_average";
        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(results);
        ArrayList<moviedata> moviedat = new ArrayList<moviedata>();
        //TODO insert movies in database
        try {
            moviedatabase_heleper heleper = new moviedatabase_heleper(getActivity());
            SQLiteDatabase sqLiteDatabase = heleper.getWritableDatabase();

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movie_detail = moviesArray.getJSONObject(i);
                moviedata moviee = new moviedata(movie_detail.getString(movie_id), movie_detail.getString(movie_poster), movie_detail.getString(movie_plot), movie_detail.getString(movie_date), movie_detail.getString(movie_title), movie_detail.getString(movie_rating));


                    ContentValues values = getContentValueFromArrayList(moviee);
                   sqLiteDatabase.insert("movie", null, values);



                moviedat.add(moviee);
            }


            sqLiteDatabase.close();
            heleper.close();
        }catch (Exception e){
            Log.e("uuu", "Error ", e);
        }
        return moviedat;

    }

    private ContentValues getContentValueFromArrayList(moviedata moviee) {
        ContentValues values = new ContentValues();
        //TODO insert data
        values.put("movie_id",moviee.getmId());
        values.put("movie_poster",moviee.getMposter());
        values.put("movie_plot",moviee.getMplot());
        values.put("movie_date",moviee.getMdate());
        values.put("movie_title",moviee.getMtitle());
        values.put("movie_rating",moviee.getMrating());
        return values;
    }


    public class fetchMoviesTask extends AsyncTask<String, Void, ArrayList<moviedata>> {

        private final String LOG_TAG = fetchMoviesTask.class.getSimpleName();



        @Override
        protected void onPostExecute(ArrayList<moviedata> moviedat) {

            mMoviesAdapter.clear();

            mMoviesAdapter.addAll(moviedat);
            // New data is back from the server.  Hooray!




        }


        @Override
        protected ArrayList<moviedata> doInBackground(String... params) {
            // if (params.length==0){return null;}
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;
            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType=sharedPreferences.getString(getString(R.string.pref_movie_key),getString(R.string.pref_movie_top_rated));

            if (unitType.equalsIgnoreCase("favourite")){
                Log.v("www","in fav"+unitType);
                moviedatabase_heleper heleper = new moviedatabase_heleper(getActivity());
                SQLiteDatabase sqLiteDatabase = heleper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.query("movie",null,"  movie_fav = 'like' ",null,null,null,null);
                return getarraylistfromcursor(cursor);
            }

            try {
                final String movie_base_url = "https://api.themoviedb.org/3/movie/"+unitType;

                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(movie_base_url).buildUpon()

                        .appendQueryParameter(APPID_PARAM, BuildConfig.TMDB_API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to movies request, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();

                Log.v(LOG_TAG, "movie string: " + moviesJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getmovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private ArrayList<moviedata> getarraylistfromcursor(Cursor cursor) {
        ArrayList<moviedata> moviedatas = new ArrayList<>();

        while (cursor.moveToNext()){

             String mId = cursor.getString(cursor.getColumnIndex("movie_id"));
             String mposter = cursor.getString(cursor.getColumnIndex("movie_poster"));
             String mplot = cursor.getString(cursor.getColumnIndex("movie_plot"));
             String mdate = cursor.getString(cursor.getColumnIndex("movie_date"));
             String mtitle = cursor.getString(cursor.getColumnIndex("movie_title"));
             String mrating = cursor.getString(cursor.getColumnIndex("movie_rating"));
             String mfav = cursor.getString(cursor.getColumnIndex("movie_fav"));

            moviedata moviedata = new moviedata( mId,   mposter,   mplot,   mdate,   mtitle,   mrating, mfav);
            moviedatas.add(moviedata);
        }
        return moviedatas;
    }


    public class CheckNetwork {
        private Context context;

        public CheckNetwork(Context context) {
            this.context = context;
        }

        public boolean isNetworkAvailable() {
            NetworkInfo activeNetworkInfo=null;
            try{
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
              activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();}
            catch (SecurityException e){e.printStackTrace();
            return false;}
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    interface interfcallback{
        void callOnclick(moviedata movieda);
    }
}


