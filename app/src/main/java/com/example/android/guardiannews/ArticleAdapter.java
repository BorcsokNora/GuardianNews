package com.example.android.guardiannews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.guardiannews.MainActivity.CATEGORY_CITY;
import static com.example.android.guardiannews.MainActivity.CATEGORY_ENVIRONMENT;
import static com.example.android.guardiannews.MainActivity.CATEGORY_GLOBAL_DEVELOPMENT;
import static com.example.android.guardiannews.MainActivity.CATEGORY_SCIENCE;
import static com.example.android.guardiannews.MainActivity.CATEGORY_TECH;
import static com.example.android.guardiannews.MainActivity.CATEGORY_WORLD;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {
    private ArrayList<Article> articleList;  // the list type that will be passed to the adapter
    private Context context;

    //the custom listener that we added as an interface to the adapter,
    //This is an input parameter for the constructor, and has to be implemented by the activity
    private OnItemClickListener listener;

    //abstract custom OnItemClickListener that has to be implemented when the adapter is constructed
    //the method is implemented within MainActivity.
    public interface OnItemClickListener {
        //the input parameter has to be one list item that will be clicked by the user
        void onItemClick(Article articleListItem);
    }

    //ViewHolder class for RecycleView
//It defines all the views from the layout that will be used to show data
    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        final TextView dateView;
        TextView timeView;
        TextView categoryView;
        TextView titleView;
        TextView trailTextView;
        TextView authorView;
        View colorIndicatorView;
        LinearLayout container;

        ArticleViewHolder(View itemView) {
            super(itemView);

            dateView = itemView.findViewById(R.id.date);
            timeView = itemView.findViewById(R.id.time);
            categoryView = itemView.findViewById(R.id.category);
            titleView = itemView.findViewById(R.id.title);
            trailTextView = itemView.findViewById(R.id.trail_text);
            authorView = itemView.findViewById(R.id.author);
            colorIndicatorView = itemView.findViewById(R.id.color_indicator);
            container = itemView.findViewById(R.id.container);
        }

    }

    //CONSTRUCTOR
    ArticleAdapter(Context context, ArrayList<Article> articleList, OnItemClickListener listener) {
        this.articleList = articleList;
        this.context = context;
        this.listener = listener;
    }

    //Overriding the 3 abstract methods of RecyclerView.Adapter superclass

    //This method returns the number of items present in the data list.
    @Override
    public int getItemCount() {
        return articleList.size();
    }

    //this method is called when the custom ViewHolder needs to be initialized.
    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We specify the layout that each item of the RecyclerView should use.
        // This is done by inflating the layout using LayoutInflater,
        // passing the output to the constructor of the custom ViewHolder.

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new ArticleViewHolder(view);
    }

    //Override the onBindViewHolder to specify the contents of each item of the RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder,
                                 int position)    //position of the list item
        {
        //get the actual item's position with the superclass' get method
        final Article articleItem = articleList.get(position);

        assert articleItem != null;

        holder.dateView.setText(articleItem.getPublicationDate());
        holder.timeView.setText(articleItem.getPublicationTime());
        holder.categoryView.setText(articleItem.getCategory());
        holder.titleView.setText(articleItem.getArticleTitle());
        holder.trailTextView.setText(articleItem.getTrailText());
        String authorName = articleItem.getAuthorName();
        if (authorName != null) {
            String nameToDisplay = getContext().getResources().getString(R.string.author_pre_tag) + articleItem.getAuthorName();
            holder.authorView.setText(nameToDisplay);
        }

        int categoryColor;
        //pick the color according to category
        switch (articleItem.getCategoryCase()) {
            case CATEGORY_TECH:
                categoryColor = getColorResource(R.color.colorCase1);
                break;
            case CATEGORY_CITY:
                categoryColor = getColorResource(R.color.colorCase2);
                break;
            case CATEGORY_SCIENCE:
                categoryColor = getColorResource(R.color.colorCase3);
                break;
            case CATEGORY_WORLD:
                categoryColor = getColorResource(R.color.colorCase4);
                break;
            case CATEGORY_ENVIRONMENT:
                categoryColor = getColorResource(R.color.colorCase5);
                break;
            case CATEGORY_GLOBAL_DEVELOPMENT:
                categoryColor = getColorResource(R.color.colorCase6);
                break;

            default:
                categoryColor = getColorResource(R.color.colorPrimaryText);

        }
        //set the color of the views
        holder.categoryView.setTextColor(categoryColor);
        holder.colorIndicatorView.setBackgroundColor(categoryColor);

        //sets listener on the article list item that will be implemented when the adapter is called
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(articleItem);
            }
        });
    }

    //Overriding the onAttachedToRecyclerView method.
    //We can simply use the superclass's implementation of this method.
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public Context getContext() {
        return context;
    }

    public void clearList() {
        articleList.clear();
    }

    public void addAllToList(List<Article> data) {
        articleList.addAll(data);
    }

    private int getColorResource(int colorResource) {
        return getContext().getResources().getColor(colorResource);
    }
}
