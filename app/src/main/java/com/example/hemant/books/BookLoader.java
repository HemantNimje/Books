package com.example.hemant.books;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;


public class BookLoader extends AsyncTaskLoader<List<Book>> {

    // Query url
    private String mUrl;
    List<Book> result;

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        if (result != null) {
            deliverResult(result);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform network request, parse response and extract list of books
        result = QueryUtils.fetchBooks(mUrl);
        return result;
    }

    @Override
    public void deliverResult(List<Book> data) {
        result = data;
        super.deliverResult(data);
    }
}
