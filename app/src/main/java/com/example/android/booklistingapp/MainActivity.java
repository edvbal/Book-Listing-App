package com.example.android.booklistingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // @LOG_TAG for logs
    public static final String LOG_TAG = MainActivity.class.getName();

    // URL for books data from Google API
    private static final String BOOKS_SEARCH_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";

    private BookAdapter bookAdapter;
    private TextView problemText;
    private ProgressBar progressBar;
    private EditText searchEditText;
    private ImageView searchClick;
    private RecyclerView bookRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private boolean isInternetConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bookAdapter = new BookAdapter(new ArrayList<Book>(), getApplicationContext());

        // Find references to the views in layout
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        searchClick = (ImageView) findViewById(R.id.search_click);
        searchEditText = (EditText) findViewById(R.id.search_text);
        problemText = (TextView) findViewById(R.id.problemText);
        bookRecyclerView = (RecyclerView) findViewById(R.id.recycler_list);
        layoutManager = new LinearLayoutManager(this);
        bookRecyclerView.setLayoutManager(layoutManager);

        // Set unnecesary views invisible
        progressBar.setVisibility(View.GONE);
        problemText.setVisibility(View.GONE);
        // @checkIfInternetConnected returns true if internet connected and false otherwise
        isInternetConnected = checkIfInternetConnected();

        // Search icon click listener
        searchClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Everytime search icon is clicked recycler view is getting cleaned and internet
                // connection is checked again.
                bookAdapter = new BookAdapter(new ArrayList<Book>(), getApplicationContext());
                bookRecyclerView.setAdapter(bookAdapter);
                isInternetConnected = checkIfInternetConnected();
                // If internet is connected text is retrieved from EditText and added to
                // BOOK_SEARCH_URL. Newly generated URL is given to BookAsyncTask to do asynchronous
                // HTTP GET request.
                if (isInternetConnected){
                    problemText.setVisibility(View.GONE);
                    String searchText = searchEditText.getText().toString();
                    if (searchText.isEmpty())
                        Toast.makeText(MainActivity.this, "First enter the book to search", Toast.LENGTH_SHORT).show();
                    else{
                        progressBar.setVisibility(View.VISIBLE);
                        BookAsyncTask asyncTask = new BookAsyncTask();
                        asyncTask.execute(BOOKS_SEARCH_URL+searchText);
                    }
                // If internet is not connected Toast message will be shown
                }else{
                    Toast.makeText(MainActivity.this, R.string.turn_on_internet,Toast.LENGTH_SHORT).show();
                    problemText.setVisibility(View.VISIBLE);
                    problemText.setText(R.string.no_internet);
                }
            }
        });
    }
    /* Save state when rotating
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putputDoubleArray(SAVE_SCORE, questionScores);
        savedInstanceState.putInt(SAVE_CURRENT_QUESTION, questionNumber);
        savedInstanceState.putDouble(SAVE_FINAL_SCORE, finalScore);
        super.onSaveInstanceState(savedInstanceState);
    }

    //----------------------------------------------------------------------------------------------
    // Restore state when rotating
    //----------------------------------------------------------------------------------------------
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        savedInstanceState.getDoubleArray(SAVE_SCORE);
        savedInstanceState.getInt(SAVE_CURRENT_QUESTION);
        savedInstanceState.getDouble(SAVE_FINAL_SCORE);
        super.onRestoreInstanceState(savedInstanceState);
    }
*/

    private boolean checkIfInternetConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }else {
            problemText.setVisibility(View.VISIBLE);
            problemText.setText(R.string.no_internet);
            return false;
        }
    }

    private class BookAsyncTask extends AsyncTask<String,Void,List<Book>>{
        // When BookAsyncTask is executed QueryUtils.fetchBooksData(String url) is being called
        // to make the HTTP GET request and extract the JSON response from server.
        @Override
        protected List<Book> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null)
                return null;
            List<Book> books;
            books = QueryUtils.fetchBooksData(urls[0]);
            return books;
        }
        // After BookAsyncTask is completed RecyclerView gets reseted and new results are being
        // shown
        @Override
        protected void onPostExecute(List<Book> books) {
            if(books == null){
                progressBar.setVisibility(View.GONE);
                problemText.setVisibility(View.VISIBLE);
                problemText.setText(R.string.cant_find_books);
                return;
            }
            progressBar.setVisibility(View.GONE);
            problemText.setVisibility(View.GONE);
            // Clear last adapter
            if (bookAdapter != null){
                bookAdapter = new BookAdapter(new ArrayList<Book>(), getApplicationContext());
                bookRecyclerView.setAdapter(bookAdapter);
            }
            bookAdapter = new BookAdapter(books,getApplicationContext());
            bookRecyclerView.setAdapter(bookAdapter);
        }

    }
    private NetworkInfo checkInternetConnection(){
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo;
    }
}
