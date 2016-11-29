package com.kimbshkorp.moviesapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kimbshkorp.moviesapp.data.moviedatabase_heleper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Intent.ACTION_VIEW;
import static com.kimbshkorp.moviesapp.R.id.author_review;
import static com.kimbshkorp.moviesapp.R.id.content_review;


public class DetailActivityFragment extends Fragment {
    boolean isfav;
    moviedata movie;
    private reviewAdapter mReviewAdapter;
    private trailerAdapter mtrailerAdapter;
    ImageView fav;
    public DetailActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("moviestillthere", movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (savedInstanceState != null) {
            movie = savedInstanceState.getParcelable("moviestillthere");
        } else {
            movie = getArguments().getParcelable(Intent.EXTRA_TEXT);
        }
        updateTrailers();
        updateReviews();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.posteer_detail);
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + movie.getMposter()).into(imageView);
        TextView plot_textView = (TextView) rootView.findViewById(R.id.plot_detail);
        plot_textView.setText(movie.getMplot());
        TextView date_textView = (TextView) rootView.findViewById(R.id.date_detail);
        date_textView.setText(movie.getMdate());
        TextView title_textView = (TextView) rootView.findViewById(R.id.title_detail);
        title_textView.setText(movie.getMtitle());
        TextView rating_textView = (TextView) rootView.findViewById(R.id.rating_title);
        rating_textView.setText(movie.getMrating() + "/10");

        fav = (ImageView) rootView.findViewById(R.id.fav_icon);
        if (movie.getFav().equalsIgnoreCase("like")){
            fav.setImageResource(R.drawable.ic_favorite_black_24dp);
        }else{
            fav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                moviedatabase_heleper heleper = new moviedatabase_heleper(getActivity());
                SQLiteDatabase sqLiteDatabase = heleper.getWritableDatabase();
                if (movie.getFav().equalsIgnoreCase("dislike")) {
                    fav.setImageResource(R.drawable.ic_favorite_black_24dp);
                    Toast.makeText(getActivity(), "added to DB", Toast.LENGTH_SHORT).show();
                    sqLiteDatabase.execSQL("update movie set movie_fav = 'like' where movie_id = "+movie.getmId());
                    movie.setFav("like");

                } else {
                    fav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    Toast.makeText(getActivity(), "removed to DB", Toast.LENGTH_SHORT).show();
                    sqLiteDatabase.execSQL("update movie set movie_fav = 'dislike' where movie_id = "+movie.getmId());
                    movie.setFav("dislike");

                }


                sqLiteDatabase.close();
                heleper.close();
            }
        });

        mReviewAdapter = new reviewAdapter(getActivity(), movie.getMreview_array());
        ListView listView_review = (ListView) rootView.findViewById(R.id.review_list);
        listView_review.setAdapter(mReviewAdapter);
        listView_review.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent reviewIntent = new Intent(ACTION_VIEW);
                reviewIntent.setData(Uri.parse(mReviewAdapter.getItem(position).getMreview_url()));
                startActivity(reviewIntent);

            }
        });


        mtrailerAdapter=new trailerAdapter(getActivity(),movie.getTrialer_array());
        GridView listView_trailer = (GridView) rootView.findViewById(R.id.trailer_list);
        listView_trailer.setAdapter(mtrailerAdapter);
        listView_trailer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent trailerIntent = new Intent(ACTION_VIEW);
                trailerIntent.setData(Uri.parse("https://www.youtube.com/watch?v="+mtrailerAdapter.getItem(position)));
                startActivity(trailerIntent);

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView_review.setNestedScrollingEnabled(true);
            listView_trailer.setNestedScrollingEnabled(true);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();



    }
    public void updateTrailers() {
        fetchtrailersTask trailerTask = new fetchtrailersTask();
        trailerTask.execute();
    }

    public void updateReviews() {
        fetchReviewTask reviewTask = new fetchReviewTask();
        reviewTask.execute();

    }
public class fetchtrailersTask extends AsyncTask<String,Void,Void>{
    @Override
    protected void onPostExecute(Void aVoid) {
mtrailerAdapter.notifyDataSetChanged();
    }

    private final String LOG_TAG = fetchtrailersTask.class.getSimpleName();

    @Override
    protected Void doInBackground(String... params) {
       
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String trailersJsonStr = null;

        try {
            final String movie_base_url = "https://api.themoviedb.org/3/movie/" + movie.getmId() + "/trailers";

            final String APPID_PARAM = "api_key";
            Uri builtUri = Uri.parse(movie_base_url).buildUpon()

                    .appendQueryParameter(APPID_PARAM, BuildConfig.TMDB_API_KEY)
                    .build();
            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Built review URI " + builtUri.toString());

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
            trailersJsonStr = buffer.toString();

            Log.v(LOG_TAG, "trialer string: " + trailersJsonStr);


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
             gettrailersDataFromJson(trailersJsonStr);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

    private void gettrailersDataFromJson(String trailersJsonStr) throws Exception{


        final String youtube_trialer = "youtube";
        final String key_trialer = "source";
        final String url_review = "url";
        JSONObject trialerJson = new JSONObject(trailersJsonStr);
        JSONArray trailerArray = trialerJson.getJSONArray(youtube_trialer);
        movie.getTrialer_array().clear();
        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject traileree = trailerArray.getJSONObject(i);
            String youtube_key=traileree.getString(key_trialer);
            //review review_result = new review(reviewee.getString(author_review), reviewee.getString(content_review), reviewee.getString(url_review));
            movie.getTrialer_array().add(youtube_key);

        }
        Log.v("trailer", "===  " + movie.getTrialer_array());

    }


    public class fetchReviewTask extends AsyncTask<String, Void, ArrayList<review>> {

        private final String LOG_TAG = fetchReviewTask.class.getSimpleName();

        @Override
        protected ArrayList<review> doInBackground(String... params) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String reviewsJsonStr = null;

            try {
                final String movie_base_url = "https://api.themoviedb.org/3/movie/" + movie.getmId() + "/reviews";

                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(movie_base_url).buildUpon()

                        .appendQueryParameter(APPID_PARAM, BuildConfig.TMDB_API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built review URI " + builtUri.toString());

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
                reviewsJsonStr = buffer.toString();

                Log.v(LOG_TAG, "review string: " + reviewsJsonStr);


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
                return getreviewDataFromJson(reviewsJsonStr);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<review> reviews) {
//                mReviewAdapter.clear();
//                mReviewAdapter.addAll(reviews);
            mReviewAdapter.notifyDataSetChanged();

        }
    }

    private ArrayList<review> getreviewDataFromJson(String mreviewsJsonStr) throws Exception {
        final String results = "results";
        final String author_review = "author";
        final String content_review = "content";
        final String url_review = "url";
        JSONObject reviewJson = new JSONObject(mreviewsJsonStr);
        JSONArray reviewArray = reviewJson.getJSONArray(results);
        movie.getMreview_array().clear();
        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject reviewee = reviewArray.getJSONObject(i);
            review review_result = new review(reviewee.getString(author_review), reviewee.getString(content_review), reviewee.getString(url_review));
            movie.getMreview_array().add(review_result);

        }
         return movie.getMreview_array();
    }
}
