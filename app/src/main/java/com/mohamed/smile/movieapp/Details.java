package com.mohamed.smile.movieapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class Details extends AppCompatActivity {

    TextView m_title,m_rate,m_overview,m_date,m_adult;
    ImageView m_poster,m_backdrop,m_favorite;
    //DataBase MyDb;
    public int IfExist ;
    String API_KEY       = "01246bd152e2002464c65f124110f2ca";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Regular.ttf")
                .setFontAttrId(R.attr.fontPath).build());
        setContentView(R.layout.fragment_details);
        //MyDb=new DataBase(this);

        String baseUrl = "http://image.tmdb.org/t/p/w185";
        Intent i=getIntent();
        final String movieId=i.getStringExtra("Id");
        final String movieTitle=i.getStringExtra("title");
        final String MoviePoster=i.getStringExtra("poster");
        final String MovieRate=i.getStringExtra("rate");
        final String movieDate=i.getStringExtra("date");
        final String MovieType=i.getStringExtra("adult");
        final String MovieOverview=i.getStringExtra("overview");
        final String MovieBackDrop=i.getStringExtra("backdrop");

        m_title=(TextView)findViewById(R.id.movie_title);
        m_rate=(TextView)findViewById(R.id.movie_rate);
        m_poster=(ImageView)findViewById(R.id.movie_poster);
        m_backdrop=(ImageView)findViewById(R.id.movie_backdrop);
        m_favorite=(ImageView)findViewById(R.id.add_favorites_movies);
        m_date=(TextView)findViewById(R.id.movie_date);
        m_adult=(TextView)findViewById(R.id.movie_type);
        m_overview=(TextView)findViewById(R.id.movie_overview);

        String URL="content://com.mohamed.smile.movieapp";
        Uri uri=Uri.parse(URL);
        Cursor res=managedQuery(uri,null," MOVIE_ID = "+movieId,null,null);
        //Cursor res=MyDb.findMovie(movieId);


        IfExist=res.getCount();
        if (IfExist==0){
            Picasso.with(this).load(R.drawable.ic_star_border_black).placeholder(R.drawable.placehohder)
                    .error(R.drawable.icn).into(m_favorite);
        }else {
            Picasso.with(this).load(R.drawable.ic_star_black).placeholder(R.drawable.placehohder)
                    .error(R.drawable.icn).into(m_favorite);
        }



        Picasso.with(this).load(baseUrl + MoviePoster).placeholder(R.drawable.not_avilable)
                .error(R.drawable.icn).into(m_poster);

        Picasso.with(this).load(baseUrl + MovieBackDrop).placeholder(R.drawable.not_avilable)
                .error(R.drawable.icn).into(m_backdrop);

        m_title.setText(movieTitle);
        m_rate.setText(MovieRate);
        m_date.setText(movieDate);
        m_adult.setText(MovieType);
        m_overview.setText(MovieOverview);


        if(CheckConnection()) {
            AsyncTraillerTask traillerTask = new AsyncTraillerTask();
            traillerTask.execute("http://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=" + API_KEY);
            AsyncReviewTask reviewTask = new AsyncReviewTask();
            reviewTask.execute("http://api.themoviedb.org/3/movie/" + movieId + "/reviews?api_key=" + API_KEY);
        }
        else {
            showDialog("Network Error","Check Your Internet Connection ...");
        }
        m_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Cursor res=MyDb.findMovie(movieId);
                if (res.getCount()==0){
                    boolean isInserted = MyDb.insertMovie(
                            movieId,
                            movieTitle,
                            movieDate,
                            MovieRate,
                            MovieType,
                            MovieOverview,
                            MoviePoster,
                            MovieBackDrop
                    );
                    if(isInserted =true) {
                        Toast.makeText(getBaseContext(), "Movie Added To Favourit", Toast.LENGTH_LONG).show();
                        Picasso.with(Details.this).load(R.drawable.ic_star_black).placeholder(R.drawable.placehohder)
                                .error(R.drawable.icn).into(m_favorite);
                    }
                    else
                        Toast.makeText(getBaseContext(),"Movie Not Added To Favourit",Toast.LENGTH_LONG).show();
                }else{
                    int delete=MyDb.deleteMovie(movieId);
                    if (delete>0) {
                        Toast.makeText(getBaseContext(), "Movie Deleted From Favourit", Toast.LENGTH_LONG).show();
                        Picasso.with(Details.this).load(R.drawable.ic_star_border_black).placeholder(R.drawable.placehohder)
                                .error(R.drawable.icn).into(m_favorite);
                    }
                    else
                        Toast.makeText(getBaseContext(),"Movie Not Deleted From Favourit",Toast.LENGTH_LONG).show();
                }*/

                String URL="content://com.mohamed.smile.movieapp";
                Uri uri=Uri.parse(URL);
                Cursor res=managedQuery(uri,null," MOVIE_ID = "+movieId,null,null);
                if (res.getCount()==0){
                    ContentValues values=new ContentValues();
                    values.put(MoviesProvider.COL_1,movieId);
                    values.put(MoviesProvider.COL_2,movieTitle);
                    values.put(MoviesProvider.COL_3,movieDate);
                    values.put(MoviesProvider.COL_4,MovieRate);
                    values.put(MoviesProvider.COL_5,MovieType);
                    values.put(MoviesProvider.COL_6,MovieOverview);
                    values.put(MoviesProvider.COL_7,MoviePoster);
                    values.put(MoviesProvider.COL_8,MovieBackDrop);

                    Uri movie_insert=getContentResolver().insert(MoviesProvider.CONTENT_URL,values);
                    Toast.makeText(getBaseContext(),movie_insert.toString(),Toast.LENGTH_LONG).show();
                    if(movie_insert !=null) {
                        Toast.makeText(getBaseContext(), "Movie Added To Favourit", Toast.LENGTH_LONG).show();
                        Picasso.with(Details.this).load(R.drawable.ic_star_black).placeholder(R.drawable.placehohder)
                                .error(R.drawable.icn).into(m_favorite);
                    }
                    else
                        Toast.makeText(getBaseContext(),"Movie Not Added To Favourit",Toast.LENGTH_LONG).show();
                }else {
                    int delete=getContentResolver().delete(uri," MOVIE_ID = "+movieId,null);
                    if (delete>0) {
                        Toast.makeText(getBaseContext(), "Movie Deleted From Favourit", Toast.LENGTH_LONG).show();
                        Picasso.with(Details.this).load(R.drawable.ic_star_border_black).placeholder(R.drawable.placehohder)
                                .error(R.drawable.icn).into(m_favorite);
                    }
                    else
                        Toast.makeText(getBaseContext(),"Movie Not Deleted From Favourit",Toast.LENGTH_LONG).show();


                }}
        });

    }
    public void showReview(String Author , String Review) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Details.this);
        builder.setCancelable(true);
        builder.setTitle(Author);
        builder.setMessage(Review);
        builder.setPositiveButton("ok",null);
        builder.show();
    }
    public void showDialog(String title , String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("ok",null);
        builder.show();
    }
    public class AsyncReviewTask extends AsyncTask<String, Void , ArrayList<Author>> {
        @Override
        protected ArrayList<Author> doInBackground(String... params) {
            String jsonString = getReviewJSON(params[0]);
            ArrayList<Author> authors = parseReviewJson(jsonString);
            return  authors;
        }

        public ArrayList<Author> parseReviewJson(String jsonString)  {
            final ArrayList<Author> authorList = new ArrayList<Author>();
            JSONObject jsonObject;
            JSONArray  jsonArray;
            JSONObject jsonObject1;

            try {

                jsonObject = new JSONObject(jsonString);
                jsonArray  = jsonObject.getJSONArray("results");
                for(int i = 0 ; i <jsonArray.length() ; i++) {

                    Author author = new Author();
                    jsonObject1 = jsonArray.getJSONObject(i);
                    author.setName( jsonObject1.getString("author"));
                    author.setOverview( jsonObject1.getString("content"));
                    authorList.add(author);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return authorList;
        }

        public String getReviewJSON(String url) {
            HttpURLConnection httpURLConnection = null;
            try {
                URL url1 = new URL(url);
                httpURLConnection = (HttpURLConnection) url1.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }
                reader.close();
                return builder.toString();
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (httpURLConnection != null) {
                    try {
                        httpURLConnection.disconnect();
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute( ArrayList<Author> dataList) {

            final ArrayList<Author> Reaviews=dataList;


            ImageView Review=(ImageView)findViewById(R.id.movie_review);


            Review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Reaviews.size()>=1){
                        showReview(Reaviews.get(0).getName(),Reaviews.get(0).getOverview());
                    }
                    else {
                        Toast.makeText(getBaseContext(),"Not Reaviews For This Movie.",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    public class AsyncTraillerTask extends AsyncTask<String, Void , ArrayList<String>> {
        @Override
        protected  ArrayList<String> doInBackground(String... params) {
            String jsonString = getTraillerJSON(params[0]);
            ArrayList<String> newData = new ArrayList<String>();
            newData = parseTraillerJson(jsonString);
            return newData;
        }

        @Override
        protected void onPostExecute(final ArrayList<String> dataList) {
            ImageView Trailler=(ImageView)findViewById(R.id.movie_trailler);
            Trailler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dataList!=null){
                        String url=dataList.get(0);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + url));
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getBaseContext(),"Not Trailler For This Movie.",Toast.LENGTH_LONG).show();

                    }
                }
            });
        }

        public String getTraillerJSON(String url) {
            HttpURLConnection httpURLConnection = null;
            try {
                URL url1 = new URL(url);
                httpURLConnection = (HttpURLConnection) url1.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line+"\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();

            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }  finally {
                if (httpURLConnection != null) {
                    try {
                        httpURLConnection.disconnect();
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return null;
        }

        public ArrayList<String> parseTraillerJson(String jsonString)  {
            ArrayList<String> dataList = new ArrayList<String>();
            JSONObject jsonObject;
            JSONArray  jsonArray;
            JSONObject object;
            try {
                jsonObject = new JSONObject(jsonString);
                jsonArray  = jsonObject.getJSONArray( "results" );
                for(int i = 0 ; i<jsonArray.length() ; i++) {
                    object = jsonArray.getJSONObject(i);
                    dataList.add(object.getString("key"));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return dataList;

        }
    }
    private boolean CheckConnection() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    }
