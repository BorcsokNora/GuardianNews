package com.example.android.guardiannews;

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

import static com.example.android.guardiannews.MainActivity.TAG;

final class ArticleUtils {
    private final static int READ_TIMEOUT_LIMIT = 10000; /* milliseconds */
    private final static int CONNECTION_TIMEOUT_LIMIT = 15000; /* milliseconds */
    private final static int SERVER_RESPONSE_OK = 200;


    private ArticleUtils() {
    }


    //create URL object from url address
    private static URL createUrl(String url) {
        URL mUrl = null;
        try {
            mUrl = new URL(url);
        } catch (MalformedURLException exception) {
            Log.e(TAG, "createUrl: ", exception);
        }
        return mUrl;
    }


    // make http request to get a json string from the server
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            Log.e(TAG, "makeHttpRequest error: null parameter");
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(READ_TIMEOUT_LIMIT);
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT_LIMIT);
            urlConnection.connect();
            // If the request was successful (response code 200), then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == SERVER_RESPONSE_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream); //see readFromStream method in step 3
            } else {
                Log.e(TAG, "Http connection problem. Response code = " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem creating url connection or retrieving the Guardian JSON results. ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies that an IOException could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // read data from input stream and create a (json) String
    private static String readFromStream(InputStream inputStream) throws IOException {
        //create a StringBuilder that will contain all data from the stream
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            //create a reader to process the data from the input stream
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            //create a buffered reader to optimize data processing
            BufferedReader reader = new BufferedReader(inputStreamReader);
            //read all lines from the input stream until there is any data line left
            String line = reader.readLine();
            while (line != null) {
                //add all the read lines to the output StringBuilder
                output.append(line);
                // read the next line of data
                line = reader.readLine();
            }
        }
        //return the StringBuilder containing all the data from the stream in String format
        return output.toString();
    }

    private static List<Article> getArticleList(String jsonString) {

        List<Article> articleArrayList = new ArrayList<>();

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        //parse and map the json string
        try {
            //define the route to the fields we need
            JSONObject root = new JSONObject(jsonString);
            JSONObject response = root.getJSONObject("response");
            JSONArray resultsArray = response.getJSONArray("results");

            //create a new article object from each item of the resultsArray
            for (int i = 0; i < resultsArray.length(); i++) {

                Article article = new Article();

                String publicationDate;
                String publicationTime;
                String articleCategory;
                String articleTitle;
                String articleUrl;
                String trailText;
                String authorName;

                //extract the relevant article data from the json string
                JSONObject jsonArticle = resultsArray.getJSONObject(i);
                publicationDate = jsonArticle.optString("webPublicationDate").substring(0, 10);
                publicationTime = jsonArticle.optString("webPublicationDate").substring(11, 16);
                articleCategory = jsonArticle.optString("sectionName");
                articleTitle = jsonArticle.optString("webTitle");
                articleUrl = jsonArticle.optString("webUrl");
                JSONObject fields = jsonArticle.getJSONObject("fields");
                trailText = fields.optString("trailText");
                JSONArray tagsArray = jsonArticle.getJSONArray("tags");
                JSONObject contributorTag;
                if (tagsArray.length() > 0) {
                    contributorTag = tagsArray.getJSONObject(0);
                    authorName = contributorTag.optString("webTitle");
                } else {
                    authorName = null;
                }

                //add the relevant article data to the corresponding field of the article object
                article.setPublicationDate(publicationDate);
                article.setPublicationTime(publicationTime);
                article.setCategory(articleCategory);
                article.setArticleTitle(articleTitle);
                article.setArticleUrl(articleUrl);
                article.setTrailText(trailText);
                if (authorName != null) {
                    if (!authorName.isEmpty()) {
                        article.setAuthorName(authorName);
                    }
                }

                //add the created article object to the list of articles
                articleArrayList.add(article);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException error: ", e);
            return null;
        }

        return articleArrayList;
    }

    static List<Article> fetchArticleData(String queryUrl) {

        // Create URL object from the url query address
        URL url = createUrl(queryUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String json = null;
        try {
            // Connect to web server
            json = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of Articles
        List<Article> articles = getArticleList(json);

        // Return the list of Articles
        return articles;
    }

}
