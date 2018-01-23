package com.example.hemant.books;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class QueryUtils {

    // LOG TAG
    private static final String LOG_TAG = QueryUtils.class.getName();

    private QueryUtils() {
    }

    /* Query Google Books api and return a {@link Book} object */
    public static List<Book> fetchBooks(String requestUrl) {

        // Sleep the thread to 2000 miliseconds to show the loading spinner
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL Object
        URL url = createUrl(requestUrl);

        // Perform HTTP requst to the URL and recieve JSON response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making HTTP request", e);
        }

        // Extract JSON response and create an {@link Event} object
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the Event
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return {@link List<Book>} object by parsing information about first book from bookJson string.
     */

    private static List<Book> extractFeatureFromJson(String bookJSON) {

        // If JSON string is empty return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create empty List to which we can add books
        List<Book> books = new ArrayList<>();

        // Try to parse JSON Response. If there is problem with the JSON format, a JSONException
        // will be thrown. Catch the exception to avoid crash
        try {
            // Convert JSON Response to JSON Object
            JSONObject JSONResponse = new JSONObject(bookJSON);

            // Get items JSONArray
            JSONArray itemsArray = JSONResponse.getJSONArray("items");

            // Loop through each item in the Items array
            for (int i = 0; i < itemsArray.length(); i++) {

                // Get the book object at position i
                JSONObject currentBookObject = itemsArray.getJSONObject(i);

                // Get the volumeInfo JSONObject
                JSONObject volumeInfoObject = currentBookObject.getJSONObject("volumeInfo");

                // Get the value for title key
                String title = volumeInfoObject.getString("title");

                // get the array for authors key
                JSONArray authorsArray = volumeInfoObject.getJSONArray("authors");

                List<String> authors = new ArrayList<>();
                // Get the list of authors from authorsArray
                for (int j = 0; j < authorsArray.length(); j++){
                    authors.add(authorsArray.getString(j));
                }

                // Create book object from title and list of authors from JSON Response
                Book book = new Book(title,authors);

                // Add book to the list of books
                books.add(book);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing book JSON results", e);
        }
        return books;

    }

}
