package com.example.hemant.books;

import java.util.ArrayList;
import java.util.List;

public class Book {

    // Title of the book
    private String mTitle;

    // Name of authors for the book
    private List<String> mAuthors;

    /**
     * Create new object of book
     *  @param title   is the title of the book
     * @param authors is the list containing name of the authors
     */
    public Book(String title, List<String> authors) {
        mTitle = title;
        mAuthors = authors;
    }

    // Get the book title
    public String getTitle() {
        return mTitle;
    }

    // Set the book title
    public void setTitle(String title) {
        mTitle = title;
    }

    // Get the authors list
    public List<String> getAuthors() {
        return mAuthors;
    }

    // Set the authors list
    public void setAuthors(ArrayList<String> authors) {
        mAuthors = authors;
    }
}
