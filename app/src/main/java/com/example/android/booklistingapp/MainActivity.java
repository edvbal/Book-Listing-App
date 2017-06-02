package com.example.android.booklistingapp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.android.booklistingapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // @LOG_TAG for logs
    public static final String LOG_TAG = MainActivity.class.getName();

    // URL for books data from Google API
    private static final String BOOKS_SEARCH_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";

    private BookAdapter bookAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private boolean isInternetConnected;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        bookAdapter = new BookAdapter(new ArrayList<Book>(), getApplicationContext());
        layoutManager = new LinearLayoutManager(this);
        binding.recyclerList.setLayoutManager(layoutManager);

        // Set unnecesary views invisible
        binding.progressBar.setVisibility(View.GONE);
        binding.problemText.setVisibility(View.GONE);
        // @checkIfInternetConnected returns true if internet connected and false otherwise
        isInternetConnected = checkIfInternetConnected();

        // Search icon click listener
        binding.searchClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Everytime search icon is clicked recycler view is getting cleaned and internet
                // connection is checked again.
                bookAdapter = new BookAdapter(new ArrayList<Book>(), getApplicationContext());
                binding.recyclerList.setAdapter(bookAdapter);
                binding.hintTextView.setVisibility(View.GONE);
                isInternetConnected = checkIfInternetConnected();
                // If internet is connected text is retrieved from EditText and added to
                // BOOK_SEARCH_URL. Newly generated URL is given to BookAsyncTask to do asynchronous
                // HTTP GET request.
                if (isInternetConnected){
                    binding.problemText.setVisibility(View.GONE);
                    String searchText = binding.searchText.getText().toString();
                    if (searchText.isEmpty())
                        Toast.makeText(MainActivity.this, "First enter the book to search", Toast.LENGTH_SHORT).show();
                    else{
                        binding.progressBar.setVisibility(View.VISIBLE);
                        new BookAsyncTask().execute(BOOKS_SEARCH_URL + searchText);
                    }
                // If internet is not connected Toast message will be shown
                }else{
                    Toast.makeText(MainActivity.this, R.string.turn_on_internet,Toast.LENGTH_SHORT).show();
                    binding.problemText.setVisibility(View.VISIBLE);
                    binding.problemText.setText(R.string.no_internet);
                }
            }
        });
    }
    private boolean checkIfInternetConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            return true;
        }else {
            binding.problemText.setVisibility(View.VISIBLE);
            binding.problemText.setText(R.string.no_internet);
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
            if(books == null || books.isEmpty()){
                binding.progressBar.setVisibility(View.GONE);
                binding.problemText.setVisibility(View.VISIBLE);
                binding.hintTextView.setVisibility(View.VISIBLE);
                binding.problemText.setText(R.string.cant_find_books);
                return;
            }
            binding.progressBar.setVisibility(View.GONE);
            binding.problemText.setVisibility(View.GONE);
            // Clear last adapter
            if (bookAdapter != null){
                bookAdapter = new BookAdapter(new ArrayList<Book>(), getApplicationContext());
                binding.recyclerList.setAdapter(bookAdapter);
            }
            bookAdapter = new BookAdapter(books,getApplicationContext());
            binding.recyclerList.setAdapter(bookAdapter);
        }

    }
}
