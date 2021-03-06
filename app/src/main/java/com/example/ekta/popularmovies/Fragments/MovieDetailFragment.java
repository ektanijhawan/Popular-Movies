package com.example.ekta.popularmovies.fragments;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.ekta.popularmovies.utilities.Constants.*;
import static com.example.ekta.popularmovies.model.ResponseKeys.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.ekta.popularmovies.model.Movie;
import com.example.ekta.popularmovies.adapters.MovieDetailAdapter;
import com.example.ekta.popularmovies.model.MovieProvider;
import com.example.ekta.popularmovies.R;
import com.example.ekta.popularmovies.database.DbHelper;
import com.example.ekta.popularmovies.utilities.VolleySingleton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDetailFragment extends Fragment {
    public ImageLoader imageLoader;
    public VolleySingleton volleySingleton;
    private Movie movie;
    public static final  String CV_ID="movieId";
    public static final String  CV_TITLE  ="title";
    public static final String CV_URLSELF ="urlSelf";
    public static final String CV_COVERIMAGE="coverimage";
    public static final String CV_AUDIENCESCORE ="audienceScore";
    public static final String CV_POPULARITY="popularity";
    public static final String  CV_RELEASEDATE=   "releaseDateTheater";
    public static final String CV_OVERVIEW = "overview";
    public static final String CV_TAGLINE ="tagline";
    public static final String CV_DURATION="duration";
    public static final String CV_GENRE="genre";
    public RequestQueue requestQueue;
    int duration = 0;
    String urlSelf;
    String imagePostUrl = "";
    public String movieID = "";
    String imageString;
    String genres = "";
    String tagline = "";
    String vote_average = "";
    int hours = 0;
    String coverImage;
    String releaseDate, overview;
    String titleStr = "";
    String fragmentValue;
    int minutes = 0;
    String popularity = "";
    ImageView imageBack;
    private Context mContext;
    String favtitle, favurlSelf, favcoverImage, favaudienceScore, favpopularity,favtagLine, favreleaseDate,favduration, favgenre, favoverview;
    Movie movieInfo;
    ImageView iv_like;
    String DurationString;
    RecyclerView mRecyclerView;
    FloatingActionMenu actionMenu; private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Activity activity;
    ImageView movieImage;
    FloatingActionButton fab;
    DbHelper dbHelper;
    String dataShare;
    TextView emptyView;
    ImageView iv_share;
    Boolean inDatabase=false;
    private ArrayList<String> mTrailerInfo = new ArrayList<>();
    private ArrayList<String> mReviewInfo = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_deatil, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_movie_detail);
        emptyView= (TextView) view.findViewById(R.id.emptyView);
        movieImage = (ImageView) view.findViewById(R.id.ivMovieImage);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
       if(!getArguments().getString("stringId").equals("-1"))
       {
            emptyView.setVisibility(View.INVISIBLE);
            mLayoutManager = new LinearLayoutManager(activity);
            mRecyclerView.setLayoutManager(mLayoutManager);
            imageBack = (ImageView) view.findViewById(R.id.ivBack);
            mLayoutManager = new LinearLayoutManager(activity);
            movie = new Movie();
            volleySingleton = VolleySingleton.getInstance(getActivity());
            requestQueue = volleySingleton.getmRequestQueue();
            imageLoader = volleySingleton.getmImageLoader();
            movieID = getArguments().getString("stringId");
            urlSelf = getArguments().getString("urlSelf");
            imageBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
            fragmentValue = getArguments().getString("fragment");

           ImageView icon = new ImageView(getActivity());
           icon.setImageResource(R.mipmap.ic_more);
           SubActionButton.Builder itemBuilder = new SubActionButton.Builder(getActivity());
           iv_like = new ImageView(getActivity());
           if (!inDatabase)
               iv_like.setImageResource(R.drawable.like_hollow_heart);
           else
               iv_like.setImageResource(R.drawable.like);

           iv_share = new ImageView(getActivity());
           iv_share.setImageResource(R.drawable.share);

           SubActionButton button1 = itemBuilder.setContentView(iv_like).build();
           SubActionButton button2 = itemBuilder.setContentView(iv_share).build();

            if (fragmentValue.equals("favourite")) {
                fab.setVisibility(View.INVISIBLE);
                actionMenu = new FloatingActionMenu.Builder(this.activity)
                        .addSubActionView(button1)
                        .attachTo(fab)
                        .build();
                fab.setVisibility(View.VISIBLE);
                favtitle = getArguments().getString("title");
                favcoverImage = getArguments().getString("coverImage");
                favaudienceScore = getArguments().getString("audienceScore");
                favpopularity = getArguments().getString("popularity");
                favtagLine = getArguments().getString("tagLine");
                favreleaseDate = getArguments().getString("releaseDate");
                favduration = getArguments().getString("duration");
                favgenre = getArguments().getString("genre");
                favoverview = getArguments().getString("overview");


            } else
            {
            fab.setVisibility(View.INVISIBLE);
                actionMenu = new FloatingActionMenu.Builder(this.activity)
                        .addSubActionView(button1)
                        .addSubActionView(button2)
                        .attachTo(fab)
                        .build();
                fab.setVisibility(View.VISIBLE);
               }



            if (fragmentValue.equals("favourite")) {

                int b = inDatabase(movieID);

                movie.setStringid(movieID);
                movie.setTitle(favtitle);
                movie.setTagLine(favtagLine);
                movie.setAudienceScore(favaudienceScore);
                movie.setPopularity(favpopularity);
                movie.setReleasedate(favreleaseDate);
                movie.setDuration(favduration);
                movie.setGenre(favgenre);
                movie.setOverview(favoverview);
                int a = imageRequest(favcoverImage, getActivity());
                mAdapter = new MovieDetailAdapter(movie, getActivity());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();


            } else if ((fragmentValue.equals("popular")))
                sendjsonRequest(movieID);

           dbHelper = new DbHelper(this.activity);
            if ((dbHelper.isInDatabase(Integer.parseInt(movieID))))
                iv_like.setImageResource(R.drawable.like);
            else
                iv_like.setImageResource(R.drawable.like_hollow_heart);

            inDatabase(movieID);

        }
       else
       {  emptyView.setVisibility(View.VISIBLE);
           mRecyclerView.setVisibility(View.INVISIBLE);
           movieImage.setVisibility(View.INVISIBLE);
           fab.setVisibility(View.INVISIBLE);
       }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(!getArguments().getString("stringId").equals("-1"))
        actionMenu.close(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        fab.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    int imageRequest(String imageString, Context context) {
        if (context != null) {
            Glide.clear(movieImage);
            Glide
                    .with(this)
                    .load(imageString)
                    .placeholder(android.R.color.transparent)
                    .crossFade()
                    .into(movieImage);
        }
        return 1;
    }


    public void sendjsonRequest(final String id) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET
                , DATA_REQUEST_PREURL + id + API_KEY

                , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                movieInfo = parseJsonResponse(response);
                inDatabase(id);

                sendjsonRequestVideos(movieID);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });


        requestQueue.add(request);
    }

    public Movie parseJsonResponse(JSONObject response) {
        if (response == null || response.length() == 0)
            return null;
        if (response != null && response.length() > 0) {
            try {
                titleStr = response.getString(TITLE);
                imagePostUrl=response.getString(BACKDROP_PATH);
                coverImage=COVER_INMAGE;
                tagline=response.getString(TAGLINE);
                duration= Integer.parseInt(response.getString(DURATION));
                releaseDate= response.getString(RELEASE_DATE);
                overview= response.getString(OVERVIEW);
                popularity=response.getString(POPULARITY);
                vote_average=response.getString(RATING);
                hours = duration / 60;
                minutes = duration % 60;
                DurationString=  hours + " hr " + minutes + " min";


                imageString=COVER_INMAGE+imagePostUrl;

                final JSONArray genreArray = response.getJSONArray(GENRES);
                for (int i = 0; i < genreArray.length(); i++) {
                    String genre = genreArray.getJSONObject(i).getString(GENRE_NAME);
                    if (i != genreArray.length() - 1)
                        genres += genre + ", ";
                    else
                        genres += genre + ".";
                }
                int a=imageRequest(imageString,getActivity());
                movie.setStringid(movieID);
                movie.setTitle(titleStr);
                movie.setCoverImage(imageString);
                movie.setUrlSelf(urlSelf);
                movie.setTagLine(tagline);
                movie.setAudienceScore(vote_average);
                movie.setPopularity(popularity);
                movie.setReleasedate(releaseDate);
                movie.setDuration(DurationString);
                movie.setGenre(genres);
                movie.setOverview(overview);


            } catch (JSONException e) {
            }

            iv_share.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    createShareIntent();
                }
            });
        }
        return movie;
    }



    public void sendjsonRequestVideos(String id)
    {

        JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET
                , DATA_REQUEST_PREURL+id+VIDEOS+API_KEY
                , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                mTrailerInfo = parseJsonResponseVidedos(response);
                sendJsonRequestReviews(movieID);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                  Toast.makeText(getActivity()," network error" + " ",Toast.LENGTH_SHORT).show();
            }
        });


        requestQueue.add(request)    ;

    }

    public ArrayList<String> parseJsonResponseVidedos(JSONObject response)
    {

        try {
            JSONArray mTrailerResultArray = response.getJSONArray("results");
            for (int i = 0; i < mTrailerResultArray.length(); i++) {
                JSONObject mTrailerObject = mTrailerResultArray.getJSONObject(i);
                mTrailerInfo.add(mTrailerObject.getString("key") + "," + mTrailerObject.getString("name")
                        + "," + mTrailerObject.getString("site") + "," + mTrailerObject.getString("size")
                        + "," + mTrailerObject.getString("type"));

                dataShare = YOUTUBE_PREURL + mTrailerObject.getString("key");
            }
        }
        catch (JSONException e)
        {}

        return mTrailerInfo;
    }


    public void sendJsonRequestReviews(String id)
    {

        JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET
                ,  DATA_REQUEST_PREURL+id+REVIEWS+API_KEY

                , new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                mReviewInfo = parseJsonResponseReviews(response);
                mAdapter = new MovieDetailAdapter(movie,mTrailerInfo,mReviewInfo,getActivity());
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                  Toast.makeText(getActivity()," network error" + " ",Toast.LENGTH_SHORT).show();
            }
        });


        requestQueue.add(request)    ;


    }

    public ArrayList<String> parseJsonResponseReviews(JSONObject response)
    {

        try {
            JSONArray mReviewResultArray = response.getJSONArray("results");
            for (int i = 0; i < mReviewResultArray.length(); i++) {
                JSONObject mReviewObject = mReviewResultArray.getJSONObject(i);
                mReviewInfo.add(mReviewObject.getString("author") + "," + mReviewObject.getString("content"));


            }
        }
        catch (JSONException e)
        {}

        return mReviewInfo;
    }

    private  void createShareIntent(){

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, dataShare);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        fab.setVisibility(View.INVISIBLE);
    }
    public int inDatabase(final String movieID)
    {
        final String id=movieID;
        dbHelper = new DbHelper(this.activity);
        if((dbHelper.isInDatabase(Integer.parseInt(movieID))))
            iv_like.setImageResource(R.drawable.like);
        else
            iv_like.setImageResource(R.drawable.like_hollow_heart);


        iv_like.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                if(!(dbHelper.isInDatabase(Integer.parseInt(id))))
                {
                    inDatabase = true;
                    iv_like.setImageResource(R.drawable.like);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(CV_ID, Integer.parseInt(id));
                    contentValues.put(CV_TITLE, titleStr);
                    contentValues.put(CV_URLSELF, urlSelf);
                    contentValues.put(CV_COVERIMAGE, imageString);
                    contentValues.put(CV_AUDIENCESCORE, vote_average);
                    contentValues.put(CV_POPULARITY, popularity);
                    contentValues.put(CV_TAGLINE, tagline);
                    contentValues.put(CV_RELEASEDATE, releaseDate);
                    contentValues.put(CV_DURATION, DurationString);
                    contentValues.put(CV_GENRE, genres);
                    contentValues.put(CV_OVERVIEW, overview);
                    activity.getContentResolver().insert(MovieProvider.CONTENT_URI, contentValues);
                    Toast.makeText(getActivity(),"\"Liked\"",Toast.LENGTH_SHORT).show();

                }
                else if((dbHelper.isInDatabase(Integer.parseInt(id)))){
                    inDatabase = false;
                    iv_like.setImageResource(R.drawable.like_hollow_heart);
                    Uri contentUri = MovieProvider.CONTENT_URI;
                    activity.getContentResolver().delete(contentUri, dbHelper.ID+"=?", new String[]{id});
                    Toast.makeText(getActivity(),"\"Unliked\"",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return 1;
    }
}