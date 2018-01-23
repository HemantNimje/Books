package com.example.hemant.books;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    // LOG TAG
    public static final String LOG_TAG = MainActivity.class.getName();

    // Base request url
    private static String BASE_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    // book request url
    private static String BOOK_REQUEST_URL;

    private static String SEARCH_KEYWORDS = null;

    private static final int BOOK_LOADER_ID = 0;

    private BookAdapter mAdapter;

    // Empty TextView
    private TextView mEmptyStateTextView;

    private ProgressBar mProgressBar;

    // Edittext to accept keywords from user
    private EditText searchKeywords;

    // Search button
    private Button searchButton;

    // Listview
    private ListView bookListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find reference to the {@link ListView} in the layout
        bookListView = findViewById(R.id.list);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        // Find reference to progress bar
        mProgressBar = findViewById(R.id.loading_spinner);

        // Make the progress bar invisible till user request books
        mProgressBar.setVisibility(View.INVISIBLE);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        bookListView.setAdapter(mAdapter);

        // Find reference to the EditText in the layout
        searchKeywords = findViewById(R.id.keywords);

        // Find reference to the Search Button in the layout
        searchButton = findViewById(R.id.search_button);

    }

    public void SearchBooks(View view) {
        // If keywords are not empty update search query
        if (!TextUtils.isEmpty(searchKeywords.getText())) {

            // Make the progress spinner visible
            mProgressBar.setVisibility(View.VISIBLE);

            // Update the {@link BOOK_REQUEST_URL} based on the user keywords
            UpdateBookRequestUrl(searchKeywords.getText().toString());
            Toast.makeText(MainActivity.this, BOOK_REQUEST_URL, Toast.LENGTH_SHORT).show();

            // Check if the device is connected to the internet
            if (isConnectedToNetwork()) {

                // Interact with the loaders using loader manager
                executeLoader();
            } else {
                //Hide the progress bar
                mProgressBar.setVisibility(View.GONE);

                //Notify no internet connection
                mEmptyStateTextView.setText(R.string.no_internet_connection);
            }


        } else {
            Toast.makeText(MainActivity.this, R.string.keyword_missing_error,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void executeLoader() {
        getLoaderManager().initLoader(BOOK_LOADER_ID, null, this);
    }

    private void UpdateBookRequestUrl(String keyword) {
        SEARCH_KEYWORDS = keyword;
        if (SEARCH_KEYWORDS.contains(" ")) {
            SEARCH_KEYWORDS = SEARCH_KEYWORDS.replaceAll("\\s", "+");
        }
        BOOK_REQUEST_URL = BASE_REQUEST_URL + SEARCH_KEYWORDS + "&maxResults=10";
    }

    public boolean isConnectedToNetwork() {
        ConnectivityManager cm = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle bundle) {
        // Create new loader for given url
        return new BookLoader(this, BOOK_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {

        //Hide the loading indicator
        mProgressBar.setVisibility(View.GONE);

        // Set empty state text to display "No books found."
        mEmptyStateTextView.setText(R.string.no_book);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }

        // Release the loader resources
        getLoaderManager().destroyLoader(BOOK_LOADER_ID);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader is reset, so clear the adapter to clear existing data.
        mAdapter.clear();
    }
}
