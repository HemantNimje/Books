package com.example.hemant.books;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BookAdapter extends ArrayAdapter<Book> {

    /**
     * Create custom constructor
     *
     * @param context is current context
     * @param books   List of book objects to display in the list
     */
    public BookAdapter(@NonNull Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    /**
     * @param position    is the position of list item to be displayed
     * @param convertView is the recycled view to populate
     * @param parent      is the parent view used to inflate
     * @return View for the position in the adapterview
     */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Check if current view can be reused else create new
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent,
                    false);
        }

        // Get the object located at this position in the list
        Book currentBook = getItem(position);

        // Find reference to the title of the listitem view
        TextView title = listItemView.findViewById(R.id.bookTitle);

        // Set the title textview with the current books title
        if (currentBook.getTitle() != null) {
            title.setText(currentBook.getTitle());
        }
        // Find reference to the author textview of the listview item
        TextView authorTextview = listItemView.findViewById(R.id.bookAuthors);

        // Set the textview with the authors from current author list
        List<String> authorList = currentBook.getAuthors();

        // String for adding all author names in it seperated by ,
        String authors = null;

        Iterator i = authorList.iterator();
        while (i.hasNext()) {
            authors += i.next();
            if (i.hasNext()) {
                authors = authors + ", ";
            }
        }

        authorTextview.setText(authors);

        return listItemView;
    }
}
