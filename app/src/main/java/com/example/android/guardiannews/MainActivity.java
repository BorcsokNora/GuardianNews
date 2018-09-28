package com.example.android.guardiannews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {
    static final String TAG = MainActivity.class.getName();      //constant tag for log entries
    static final String ORDER_BY_NEWEST = "Newest";      //constant cases for making list order
    static final String ORDER_BY_OLDEST = "Oldest";
    ArticleAdapter adapter;     //custom adapter to handle the ListView

    String baseUrl = "";

    // The list containing the query urls
    ArrayList<String> urlList;

    //Constant value for the loader ID - we use only one loader.
    private static final int GUARDIAN_LOADER_ID = 1;

    //constants indicating the different categories
    static final int CATEGORY_TECH = 1;
    static final int CATEGORY_SCIENCE = 2;
    static final int CATEGORY_CITY = 3;
    static final int CATEGORY_WORLD = 4;
    static final int CATEGORY_ENVIRONMENT = 5;
    static final int CATEGORY_GLOBAL_DEVELOPMENT = 6;

    //View to show empty state message when no article can be shown
    TextView emptyStateTextView;

    //Spinning progress bar for networking
    View loadingSpinner;

    //OnItemClickListener
    //This listener calls back when a list item is tapped
    //clicking on the list item opens the web url of the article in the phone's browser app
    //The listener is passed to the custom RecyclerView adapter as an input parameter
    private ArticleAdapter.OnItemClickListener listener = new ArticleAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Article articleListItem) {
            if (adapter != null && articleListItem != null) {
                String url = articleListItem.getArticleUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (url != null && intent.resolveActivity(adapter.getContext().getPackageManager()) != null) {
                    intent.setData(Uri.parse(url));
                    adapter.getContext().startActivity(intent);
                }
            }
        }
    };

    //implement loader callback method to create loader for background networking, fetching data via a series of query urls
    @NonNull
    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //This is the default category list to be used if the preference key can't be found
        HashSet<String> defaultCategorySelection = new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.categories_default_values)));

        //get the categories selected by the user (or the default selection)
        Set<String> categoryKeySet = sharedPrefs.getStringSet(getString(R.string.settings_select_category_key), defaultCategorySelection);

        //create an array of the category keys referring to the topic of article, that will be used as "section" parameter for the query
        String[] categoryKeys = categoryKeySet.toArray(new String[categoryKeySet.size()]);

        urlList = new ArrayList<>();
        //this for-each loop creates a list of Uris - each for one article category selected by the user
        for (String categoryKey : categoryKeys) {
            // getString retrieves a String value from the preferences.
            // The second parameter is the default value for this preference.
            String listLength = sharedPrefs.getString(
                    getString(R.string.settings_list_items_limit_key),
                    getString(R.string.settings_list_items_limit_default));

            // parse breaks apart the URI String that's passed into its parameter
            Uri baseUri = Uri.parse(baseUrl);

            // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
            Uri.Builder uriBuilder = baseUri.buildUpon();

            // Append query parameter and its value.
            uriBuilder.appendQueryParameter("section", categoryKey);
            uriBuilder.appendQueryParameter("show-tags", "contributor");
            uriBuilder.appendQueryParameter("show-fields", "trailText");
            uriBuilder.appendQueryParameter("page-size", listLength);
            uriBuilder.appendQueryParameter("api-key", "f308e9ec-fba1-4ab0-a97c-bb33a7b2ecbd");

            // Add the complete Uri to the list of query urls
            urlList.add(uriBuilder.toString());
        }

        // Return the list of completed uris containing the query urls
        return new ArticleLoader(MainActivity.this, urlList);
    }

    //implement loader callback method to update the UI with the result
    @Override
    public void onLoadFinished(@NonNull Loader<List<Article>> loader, List<Article> data) {
        //Loading spinner spins on UI during the downloading of data
        //When the background work is done, the spinning progress bar gets hidden
        hideProgressBar();

        // myAdapter.setMyDataMethod(data);
        // Clear the adapter of previous article data
        adapter.clearList();

        // If there is a valid list of Articles, then add them to the adapter's data set.
        // This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            //Set the order of the list according to the user's settings (or the default setting)
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String orderBy = sharedPrefs.getString(
                    getString(R.string.settings_order_by_key),
                    getString(R.string.settings_order_by_default));

            switch (orderBy) {
                case ORDER_BY_NEWEST:
                    //based on the publication date, sort the articles in a decreasing order
                    Collections.sort(data, Collections.reverseOrder());
                    break;
                case ORDER_BY_OLDEST:
                    //based on the publication date, sort the articles in an increasing order
                    Collections.sort(data);
                    break;
            }

            //update the list with the downloaded articles
            adapter.addAllToList(data);
            //notify the adapter to refresh the view
            adapter.notifyDataSetChanged();

            // if there is no article to show, set the empty state message to the UI
            //the emptyStateTextView is already connected to the ListView which decides to use it or not
        } else {
            if (isConnectedToNetwork()) {
                emptyStateTextView.setText(R.string.empty_list);
            }
        }
    }

    //implement loader callback method which informs us when the data from our loader is no longer valid
    // so we should empty all the data provided by the loader
    @Override
    public void onLoaderReset(@NonNull Loader<List<Article>> loader) {
        //we have only one loader in this example, so we don't need to use the loader parameter (which indicates the loader object to be reset)
        adapter.clearList();
        //here we delete the adapter data, so it is no longer visible in the UI ListView
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find a reference to the empty state view in the layout
        emptyStateTextView = findViewById(R.id.empty_state_text);
        //find a reference to the RecyclerView in the layout
        RecyclerView newsList = findViewById(R.id.article_list);

        //set a LinearLayoutManager on the recycleView to get a vertically scrollable list
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        newsList.setLayoutManager(layoutManager);

        //set a divider between the list items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(newsList.getContext(),
                layoutManager.getOrientation());
        newsList.addItemDecoration(dividerItemDecoration);

        //ArrayList that will contain all the downloaded articles
        ArrayList<Article> articleList = new ArrayList<>();
        //create a new adapter that takes an empty list of Articles, and an implemented OnItemClickListener as input
        adapter = new ArticleAdapter(MainActivity.this, articleList, listener);
        //set the adapter on the listView, so the list can be populated in the user interface
        newsList.setAdapter(adapter);

        baseUrl = getString(R.string.base_url);
        if (isConnectedToNetwork()) {
        /*
		get the loader manager and initialize a loader
		the loader id is indicated by a constant as we have only one loader
		bundle is null as we don't use an existing loader
		this (the main activity) is the actual object that should receive the callbacks and the data
             */
            getSupportLoaderManager().initLoader(GUARDIAN_LOADER_ID, null, this);
        } else {
            hideProgressBar();
            emptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    //Override onCreateOptionsMenu to put Options Menu in app bar
    // This method initialize the contents of the Activity's options menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //onOptionsItemSelected method specifies to open the SettingsActivity when the user selects the Options Menu
    //This method is where we can setup the specific action that occurs when any of the items in the Options Menu are selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //To determine which item was selected and what action to take we call getItemId
        //the menu item's unique ID is defined by the android:id attribute in the menu resource
        int id = item.getItemId();
        // check which item was selected by the user and take an action accordingly
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //check if there is available internet connection
    boolean isConnectedToNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }

    //hide progress bar after downloading the articles
    void hideProgressBar() {
        loadingSpinner = findViewById(R.id.loading_spinner);
        loadingSpinner.setVisibility(View.GONE);
    }
}
