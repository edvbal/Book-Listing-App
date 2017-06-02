package com.example.android.booklistingapp;

/**
 * Created by Edvinas on 01/06/2017.
 */

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Helper methods related to requesting and receiving books data from Google books API.
 */
public final class QueryUtils {
    /** Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static List<Book> fetchBooksData(String requestUrl){
        // Create URL object
        URL url = createURL(requestUrl);

        // Perform HTTP request and receive a JSON response back
        String jsonResponse = null;
        jsonResponse = makeHttpRequest(url);
        // Extract relevant fields from the JSON response and create an (@link Book) object
        List<Book> books = extractFromJSON(jsonResponse);
        return books;
    }

    private static List<Book> extractFromJSON(String jsonResponse) {
        if(TextUtils.isEmpty(jsonResponse))
            return null;

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the jsonResponse. If there's a problem with the way the JSON
        // is formatted, a JSONException object will be thrown.
        try {
            // build up a list of Book objects with the corresponding data.
            JSONObject baseJsonObject = new JSONObject(jsonResponse);
            JSONArray booksArray= baseJsonObject.getJSONArray("items");
            for (int i = 0; i < booksArray.length(); i++){
                // Get each book from the array when the cycle spins
                // Assign title value to @String book
                JSONObject currentBook = booksArray.getJSONObject(i);

                // Author is in volumeInfo JSON array
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                String title = volumeInfo.getString("title");

                String authors = "";
                if (volumeInfo.has("authors")) {
                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                    for (int j = 0; j < authorsArray.length(); j++){
                        if ( j+1 == authorsArray.length())
                            authors += authorsArray.getString(j) + ". ";
                        else
                            authors += authorsArray.getString(j) + ", ";
                    }
                } else authors = "There are no authors";
                Book book = new Book(authors,title);
                books.add(book);
            }
        } catch (JSONException e){
            Log.e(LOG_TAG,"Problem parsing book JSON results",e);
        }
        return books;
    }

    private static String makeHttpRequest(URL url) {
        String jsonResponse = null;

        // If the url is null then return from the method
        if (url == null)
            return jsonResponse;

        HttpsURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* miliseconds */);
            urlConnection.setConnectTimeout(15000 /* miliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
        
            // If the request was successfull (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e (LOG_TAG,"Error HTTP request response code : " + urlConnection.getResponseCode());
            }
        }catch (ProtocolException e){
            Log.e(LOG_TAG, "Protocol error", e);
        }catch (IOException e){
            Log.e(LOG_TAG, "Protocol error", e);
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    private static URL createURL(String requestUrl) {
        URL url = null;
        if (!requestUrl.isEmpty()){
            try {
                url = new URL(requestUrl);
            } catch (MalformedURLException e){
                Log.e(LOG_TAG,"Error with creating URL",e );
            }
        }
        return url;
    }

}
