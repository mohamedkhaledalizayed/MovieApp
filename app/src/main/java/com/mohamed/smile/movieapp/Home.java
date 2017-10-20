package com.mohamed.smile.movieapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Home extends AppCompatActivity {

    //widgets
     ProgressBar progressBar;
     private RecyclerView recyclerView;

    //variables
      ArrayList<Helper> arrayList ;
      String Id            = "id";
      String Results       = "results" ;
      String PosterPath    = "poster_path" ;
      String ReleaseDate   = "release_date" ;
      String Overview      = "overview" ;
      String MovieTitle    = "original_title";
      String VoteAverage   = "vote_average";
      String Adult         = "adult";
      String BackDropBath  ="backdrop_path";
     // DataBase MyDb;
      int IfExist;
      String API_KEY       = "01246bd152e2002464c65f124110f2ca";
      String key           = "key";

      String s_Movie_Title;
      String s_Movie_Poster;
      String s_Movie_BackDrop;
      String s_Movie_Rate;
      String s_Movie_Date;
      String s_Movie_Type;
      String s_Movie_OverView;
      String s_Movie_Id;
      int    s_Movie_Postion;

    private AlbumsAdapter adapter;
    private List<Helper>  albumList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Calligraphy lib.
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Regular.ttf")
                .setFontAttrId(R.attr.fontPath).build());
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize UIL.
        ImageLoaderConfiguration mConfiguration =
                new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(mConfiguration);
       // MyDb=new DataBase(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        albumList = new ArrayList<>();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if( CheckConnection() ) {
            progressBar.setVisibility(View.VISIBLE);
            AsyncMoviesTask asyncMoviesTask = new AsyncMoviesTask();
            asyncMoviesTask.execute("http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY);
        }else {
            showDialog("Network Error", "Check Your Internet Connection ...");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.highestrated_movies)
        {
            if( CheckConnection())
            {
                progressBar.setVisibility(View.VISIBLE);
                AsyncMoviesTask astask = new AsyncMoviesTask();
                astask.execute("http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY);
            }
            else {
                showDialog("NETWORK ERROR ", "Chech Your Internet Connection ...");
            }
            return true;
        }
        else if (id == R.id.favorites_movies)
        {
            getFavoriteMovies();
            return true;
        }

        else if (id == R.id.popular_movies)
        {
            if( CheckConnection())
            {
                progressBar.setVisibility(View.VISIBLE);
                AsyncMoviesTask astask = new AsyncMoviesTask();
                astask.execute("http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY);
            }
            else{
                showDialog("NETWORK ERROR ", "Chech Your Internet Connection ...");
            }
            return true;
        }
        else if (id == R.id.about_me) {
            //showDialog("My ProFile","Coming Soon :)");
            recyclerView.smoothScrollToPosition(0);
            return true;
        }
        else if (id==R.id.exit)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Exit ");
            builder.setMessage("Do You Want To Close The App ?");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();        }
        return super.onOptionsItemSelected(item);
    }

    public boolean CheckConnection() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {
        Context mContext;
        List<Helper> albumList;
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title, count;
            public ImageView thumbnail, overflow;
            View posterTitleBackground;
            public MyViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.title);
                count = (TextView) view.findViewById(R.id.count);
                thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
                overflow = (ImageView) view.findViewById(R.id.overflow);
                posterTitleBackground=(View)view.findViewById(R.id.posterTitleBackground);

            }
        }

        public AlbumsAdapter(Context mContext, List<Helper> albumList) {
            this.mContext = mContext;
            this.albumList = albumList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.album_card, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            Helper album = albumList.get(position);
            String baseUrl = "http://image.tmdb.org/t/p/w185";
            final String    Movie_Title = album.getTitle();
            final String    poster      = album.getPoster();
            final String    rate        =album.getVoteAverage();
            final String    date        =album.getDate();
            final String    overview    =album.getOverview();
            final String    adult       =album.getAdult();
            final String    backdrop    =album.getBackDropBath();
            final String    m_id        =album.getId();

            DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .cacheInMemory(false)
                    .displayer(new FadeInBitmapDisplayer(1500))
                    .resetViewBeforeLoading(true)
                    .showImageOnFail(R.drawable.placeholder_movie_details_image)
                    .showImageForEmptyUri(R.drawable.placeholder_movie_details_image)
                    .build();

            ImageLoader.getInstance()
                    .displayImage(baseUrl + poster,
                            holder.thumbnail, mOptions,
                            new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view,
                                                              Bitmap loadedImage) {
                                    Palette p = Palette.from(loadedImage).generate();
                                    holder.posterTitleBackground.setBackgroundColor(p.getVibrantColor(0));
                                    if (p.getVibrantColor(0) == Color.TRANSPARENT) {
                                        holder.posterTitleBackground.setBackgroundColor(p.getMutedColor(0));
                                    }


                                    holder.posterTitleBackground.getBackground().setAlpha(160);
                                }
                            });
            //Picasso.with(mContext).load(baseUrl + poster).placeholder(R.drawable.placehohder)
                    //.error(R.drawable.icn).into(holder.thumbnail);
            holder.title.setText(Movie_Title);
            holder.count.setText("Average Rate : "+rate);

            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(Home.this,Details.class);
                    intent.putExtra("Id",m_id);
                    intent.putExtra("title",Movie_Title);
                    intent.putExtra("poster",poster);
                    intent.putExtra("rate",rate);
                    intent.putExtra("date",date);
                    intent.putExtra("overview",overview);
                    intent.putExtra("adult",adult);
                    intent.putExtra("backdrop",backdrop);
                    //If The Device Is Tablet
                    //The Source For This ===>https://developer.android.com/guide/practices/tablets-and-handsets.html
                    if(findViewById(R.id.fragment_1) != null)
                    {
                        tablet(position,m_id);
                    }
                    else {
                        startActivity(intent);
                    }
                }
            });
            holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(holder.overflow,m_id);
                }
            });
        }

        /**
         * Showing popup menu when tapping on 3 dots
         */
        private void showPopupMenu(View view,String id) {
            // inflate menu
            PopupMenu popup = new PopupMenu(mContext, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_album, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_add_favourite:
                            Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.action_play_next:
                            Toast.makeText(mContext, "Play The Trailler", Toast.LENGTH_SHORT).show();
                            return true;
                        default:
                    }
                    return false;
                }
            });
            popup.show();
        }

        @Override
        public int getItemCount() {
            return albumList.size();
        }
    }

    public void tablet(int i ,String m_id){
        final int position=i;
        final TextView MovieType        = (TextView) findViewById(R.id.movie_type);
        final TextView MovieTitle        = (TextView) findViewById(R.id.movie_title);
        final TextView MovieYear         = (TextView) findViewById(R.id.movie_date);
        final TextView MovieVote  = (TextView) findViewById(R.id.movie_rate);
        final ImageView MovieFavourite =(ImageView)findViewById(R.id.add_favorites_movies);
        TextView MovieOverView     = (TextView) findViewById(R.id.movie_overview);
        ImageView MoviePoster              = (ImageView) findViewById(R.id.movie_poster);
        ImageView MovieBackDrop   =(ImageView)findViewById(R.id.movie_backdrop);
        String URL="content://com.mohamed.smile.movieapp";
        Uri uri=Uri.parse(URL);
        Cursor res=managedQuery(uri,null," MOVIE_ID = "+m_id,null,null);


        IfExist=res.getCount();
        if (IfExist==0){
            Picasso.with(this).load(R.drawable.ic_star_border_black).placeholder(R.drawable.placehohder)
                    .error(R.drawable.icn).into(MovieFavourite);
        }else {
            Picasso.with(this).load(R.drawable.ic_star_black).placeholder(R.drawable.placehohder)
                    .error(R.drawable.icn).into(MovieFavourite);
        }
        s_Movie_Title=arrayList.get(position).getTitle();
        s_Movie_Date =arrayList.get(position).getDate();
        s_Movie_Rate =arrayList.get(position).getVoteAverage();
        s_Movie_OverView=arrayList.get(position).getOverview();
        s_Movie_Type =arrayList.get(position).getAdult();
        s_Movie_Id   =arrayList.get(position).getId();
        s_Movie_Poster=arrayList.get(position).getPoster();
        s_Movie_BackDrop=arrayList.get(position).getBackDropBath();
        s_Movie_Postion=position;
        checkStatus(m_id);
        MovieTitle.setText(s_Movie_Title);
        MovieYear.setText(s_Movie_Date);
        if ((s_Movie_Type).equalsIgnoreCase("false")) {
            MovieType.setText("For All");
        }else {
            MovieType.setText("For Adult");
        }
        MovieVote.setText(s_Movie_Rate);
        MovieOverView.setText(s_Movie_OverView);

        String baseUrl = "http://image.tmdb.org/t/p/w185";
        Picasso.with(getApplicationContext()).load(baseUrl+s_Movie_Poster)
                .placeholder(R.drawable.placehohder).error(R.drawable.icn).into(MoviePoster);
        Picasso.with(getApplicationContext()).load(baseUrl+s_Movie_BackDrop)
                .placeholder(R.drawable.placehohder).error(R.drawable.icn).into(MovieBackDrop);

        MovieFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String movieTitle=MovieTitle.getText().toString();
                String movieDate =MovieYear.getText().toString();
                String movieRate =MovieVote.getText().toString();
                String movieType =MovieType.getText().toString();
                String moviePoster=arrayList.get(position).getPoster();
                String movieOverView=arrayList.get(position).getOverview();
                String movieId=arrayList.get(position).getId();
                String MovieBack=arrayList.get(position).getBackDropBath();
                //Cursor res=MyDb.findMovie(movieId);

                String URL="content://com.mohamed.smile.movieapp";
                Uri uri=Uri.parse(URL);
                Cursor res=managedQuery(uri,null," MOVIE_ID = "+movieId,null,null);
              if (res.getCount()==0){
                  ContentValues values=new ContentValues();
                  values.put(MoviesProvider.COL_1,movieId);
                  values.put(MoviesProvider.COL_2,movieTitle);
                  values.put(MoviesProvider.COL_3,movieDate);
                  values.put(MoviesProvider.COL_4,movieRate);
                  values.put(MoviesProvider.COL_5,movieType);
                  values.put(MoviesProvider.COL_6,movieOverView);
                  values.put(MoviesProvider.COL_7,moviePoster);
                  values.put(MoviesProvider.COL_8,MovieBack);

                  Uri movie_insert=getContentResolver().insert(MoviesProvider.CONTENT_URL,values);
                  Toast.makeText(getBaseContext(),movie_insert.toString(),Toast.LENGTH_LONG).show();
                    if(movie_insert !=null) {
                        Toast.makeText(getBaseContext(), "Movie Added To Favourit", Toast.LENGTH_LONG).show();
                        Picasso.with(Home.this).load(R.drawable.ic_star_black).placeholder(R.drawable.placehohder)
                                .error(R.drawable.icn).into(MovieFavourite);
                    }
                    else
                        Toast.makeText(getBaseContext(),"Movie Not Added To Favourit",Toast.LENGTH_LONG).show();
                }else{
                  int delete=getContentResolver().delete(uri," MOVIE_ID = "+movieId,null);
                  if (delete>0) {
                      Toast.makeText(getBaseContext(), "Movie Deleted From Favourit", Toast.LENGTH_LONG).show();
                      Picasso.with(Home.this).load(R.drawable.ic_star_border_black).placeholder(R.drawable.placehohder)
                              .error(R.drawable.icn).into(MovieFavourite);
                  }
                  else
                      Toast.makeText(getBaseContext(),"Movie Not Deleted From Favourit",Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    private void checkStatus(String movie_id) {
        if(CheckConnection()) {
            AsyncTraillersTask asyncTraillersTask = new AsyncTraillersTask();
            asyncTraillersTask.execute("http://api.themoviedb.org/3/movie/" + movie_id + "/videos?api_key=" + API_KEY);
            AsyncReviewTask asyncReviewTask = new AsyncReviewTask();
            asyncReviewTask.execute("http://api.themoviedb.org/3/movie/" + movie_id + "/reviews?api_key=" + API_KEY);
        }
        else {
            showDialog("Error","Check Your Internet Connection ...");
        }
    }

    private void getFavoriteMovies() {
        StringBuffer buffer=new StringBuffer();
        final ArrayList<Helper> list = new ArrayList<Helper>();

        String URL="content://com.mohamed.smile.movieapp";
        Uri uri=Uri.parse(URL);
        Cursor res=managedQuery(uri,null,null,null,null);
        if (res.getCount() == 0) {
            showDialog("Sorry", "No Movies Found");
            return;
        }
        else {
            while (res.moveToNext()) {
                buffer.append("ID :"+res.getString(0)+"\n");
                buffer.append("Name :"+res.getString(1)+"\n");
                buffer.append("AGE :"+res.getString(2)+"\n");
                buffer.append("ADDRESS :"+res.getString(3)+"\n");
                buffer.append("ID :"+res.getString(4)+"\n");
                buffer.append("Name :"+res.getString(5)+"\n");
                buffer.append("AGE :"+res.getString(6)+"\n");
                //showDialog("",buffer.toString());
                Helper helper = new Helper();
                helper.setId(res.getString(0));
                helper.setTitle((res.getString(1)).toString());
                helper.setDate( (res.getString(2)).toString());
                helper.setVoteAverage( (res.getString(3)).toString());
                helper.setAdult(res.getString(4));
                helper.setOverview((res.getString(5)).toString());
                helper.setPoster((res.getString(6)).toString());
                helper.setBackDropBath(res.getString(7).toString());
                list.add(helper);
            }
        }

        if (list.isEmpty())
            showDialog("Empty", "No Data about movie !!");
        else {
            adapter = new AlbumsAdapter(Home.this, list);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);


        }

    }

    public void showReview(String Author , String Review) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
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

    public class AsyncMoviesTask extends AsyncTask<String, Void , ArrayList<Helper>> {

        //Get The Information and Pass It To onPostExecute Method
        @Override
        protected  ArrayList<Helper> doInBackground(String... params) {
            String json = getMovieJSON(params[0]);
            ArrayList<Helper> List = new ArrayList<Helper>();
            List = parseMovieJson(json);
            return List;
        }

        //Take The Data From doInBackground And Make Changes In The Activity
        @Override
        protected void onPostExecute(final ArrayList<Helper> helperArrayList) {

            if(helperArrayList ==null){
                showDialog("Error", "No Data Available ...");
            }
            else {
                adapter = new AlbumsAdapter(Home.this, helperArrayList);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(adapter);
            }

            //When GridView Load Images ... Gone
            progressBar.setVisibility(View.GONE);
            arrayList = helperArrayList ;

        }

        //Take The Url And Get Json From The Website
        public String getMovieJSON(String url) {
            HttpURLConnection httpURLConnection = null;
            try {
                URL url1 = new URL(url);
                httpURLConnection = (HttpURLConnection) url1.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String s;
                while ((s = reader.readLine()) != null) {
                    builder.append(s+"\n");
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

        //Take The Json Data From getMovieJSON And Return Useful And Readable Information Then Return To doInBackground Method
        public ArrayList<Helper> parseMovieJson(String json)  {
            ArrayList<Helper> array_List = new ArrayList<Helper>();
            JSONObject jsonObject;
            JSONArray jsonArray;
            JSONObject jsonObject1;
            try {
                jsonObject = new JSONObject(json);
                jsonArray  = jsonObject.getJSONArray( Results );
                for(int i = 0 ; i<jsonArray.length() ; i++) {
                    jsonObject1 = jsonArray.getJSONObject(i);
                    Helper helper = new Helper();
                    helper.setId(jsonObject1.getString(Id));
                    helper.setTitle(jsonObject1.getString(MovieTitle));
                    helper.setPoster(jsonObject1.getString(PosterPath));
                    helper.setOverview(jsonObject1.getString(Overview));
                    helper.setDate(jsonObject1.getString(ReleaseDate));
                    helper.setVoteAverage(jsonObject1.getString(VoteAverage));
                    helper.setAdult(jsonObject1.getString(Adult));
                    helper.setBackDropBath(jsonObject1.getString(BackDropBath));
                    array_List.add(helper);
                }
            }
            catch (Exception e)
            {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
            return array_List;
        }
    }

    public class AsyncTraillersTask extends AsyncTask<String, Void , ArrayList<String>> {

        @Override
        protected  ArrayList<String> doInBackground(String... params) {

            String json = getTraillerJSON(params[0]);
            ArrayList<String> new_arraylist = new ArrayList<String>();
            new_arraylist = parseTraillerJson(json);
            return new_arraylist;
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

        public ArrayList<String> parseTraillerJson(String json)  {

            ArrayList<String> dataList = new ArrayList<String>();
            JSONObject jsonObject;
            JSONArray  jsonArray;
            JSONObject object;
            try {
                jsonObject = new JSONObject(json);
                jsonArray  = jsonObject.getJSONArray( Results );
                for(int i = 0 ; i<jsonArray.length() ; i++) {
                    object = jsonArray.getJSONObject(i);
                    dataList.add(object.getString(key));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return dataList;

        }
    }

    public class AsyncReviewTask extends AsyncTask<String, Void , ArrayList<Author>> {
        @Override
        protected ArrayList<Author> doInBackground(String... params) {
            String json = getReviewJSON(params[0]);
            ArrayList<Author> authors = parseReviewJson(json);
            return  authors;
        }





        @Override
        protected void onPostExecute( ArrayList<Author>  dataList) {

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

        public ArrayList<Author> parseReviewJson(String json)  {
            final ArrayList<Author> authorList = new ArrayList<Author>();
            JSONObject jsonObject;
            JSONArray  jsonArray;
            JSONObject jsonObject1;

            try {

                jsonObject = new JSONObject(json);
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
    }


}
