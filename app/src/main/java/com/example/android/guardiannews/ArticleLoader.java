package com.example.android.guardiannews;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.guardiannews.ArticleUtils.fetchArticleData;

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {
    private ArrayList<String> urls = null;     //the list of query urls received as parameter for the loader
    private List<Article> articleList;    //this is the variable for the list of articles

    //public constructor calling the superclass' (AsyncTaskLoader's) method
    //The second parameter is a single String queryUrl, as in the app we use only one url address
    ArticleLoader(Context context, ArrayList<String> queryUrl) {
        super(context);

        //return early if queryUrl is empty
        if (queryUrl != null) {
            urls = queryUrl;
        }
    }

    //implementing the superclass' abstract method
    @Override
    public List<Article> loadInBackground() {
        articleList = new ArrayList<>();
        for (int i = 0; i < (urls.size()); i++) {
            //pass each url from the list of query urls
            //the queries are made for specific categories like technology, cities, etc. selected by the user
            //fetch the data from the server and create a list of specific Articles
            List<Article> categoryArticles = fetchArticleData(urls.get(i));

            //check if the list of specific articles is not empty
            if (categoryArticles != null) {
                //add the elements to the list of all articles
                articleList.addAll(categoryArticles);
            }

        }

        //return list of articles
        return articleList;
    }

    //this method is triggered automatically from initLoader when starting the background work
    @Override
    protected void onStartLoading() {
        //check if articles data is already downloaded
        if (articleList != null) {
            // Use cached data
            deliverResult(articleList);
        } else {
            // If there is no available data, begin the download
            // forceLoad() triggers the implemented loadInBackground() method
            forceLoad();
        }
    }

}
