package com.example.android.myuknews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * An {@link UKNewsAdapter} knows how to create a list item layout for each news
 * in the data source (a list of {@link UKNews} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class UKNewsAdapter extends ArrayAdapter<UKNews> {

    /**
     * Constructs a new {@link UKNewsAdapter}.
     *
     * @param context of the app
     * @param news    is the list of news, which is the data source of the adapter
     */
    public UKNewsAdapter(Context context, List<UKNews> news) {
        super(context, 0, news);
    }

    /**
     * Returns a list item view that displays information about the news at the given position
     * in the list of news.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.uknews_list_item, parent, false);
        }

        // Find the news at the given position in the list
        UKNews currentNews = getItem(position);

        // Find the TextView with view ID title
        TextView titleView = (TextView) listItemView.findViewById(R.id.news_title);
        // Display the magnitude of the current earthquake in that TextView
        titleView.setText(currentNews.getTitle());

        // Find the TextView with view ID title
        TextView sectionNameView = (TextView) listItemView.findViewById(R.id.section_name);
        // Display the magnitude of the current earthquake in that TextView
        sectionNameView.setText(currentNews.getSectionName());

        // Find the TextView with view ID title
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        // Display the magnitude of the current earthquake in that TextView
        dateView.setText(currentNews.getDates());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}
