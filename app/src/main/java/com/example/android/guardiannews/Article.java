package com.example.android.guardiannews;

import android.support.annotation.NonNull;

import static com.example.android.guardiannews.MainActivity.CATEGORY_CITY;
import static com.example.android.guardiannews.MainActivity.CATEGORY_ENVIRONMENT;
import static com.example.android.guardiannews.MainActivity.CATEGORY_GLOBAL_DEVELOPMENT;
import static com.example.android.guardiannews.MainActivity.CATEGORY_SCIENCE;
import static com.example.android.guardiannews.MainActivity.CATEGORY_TECH;
import static com.example.android.guardiannews.MainActivity.CATEGORY_WORLD;

public class Article implements Comparable<Article> {

    private String publicationDate;
    private String publicationTime;
    private String articleTitle;
    private String articleUrl;
    private String trailText;
    private String authorName;
    private String category;

    //constants indicating article categories
    private final String TECHNOLOGY = "Technology";
    private final String SCIENCE = "Science";
    private final String CITIES = "Cities";
    private final String WORLD = "World news";
    private final String GLOBAL_DEV = "Global development";
    private final String ENVIRONMENT = "Environment";

    //implements Comparable interface's method
    public int compareTo(@NonNull Article other) {
        //compare articles based on date of publication
        //if dates are the same than compare them based on time of publication
        if (publicationDate.equals(other.getPublicationDate())) {
            return publicationTime.compareTo(other.getPublicationTime());
        }
        return publicationDate.compareTo(other.getPublicationDate());
    }

    // Constructs a new Article object containing data extracted from the Guardian API

    Article() {
        this.publicationDate = null;
        this.publicationTime = null;
        this.articleTitle = null;
        this.articleUrl = null;
        this.trailText = null;
        this.authorName = null;
        this.category = null;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getPublicationTime() {
        return publicationTime;
    }

    public void setPublicationTime(String publicationTime) {
        this.publicationTime = publicationTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getTrailText() {
        return trailText;
    }

    public void setTrailText(String trailText) {
        this.trailText = trailText;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getCategoryCase() {
        switch (category) {
            case TECHNOLOGY:
                return CATEGORY_TECH;
            case SCIENCE:
                return CATEGORY_SCIENCE;
            case CITIES:
                return CATEGORY_CITY;
            case WORLD:
                return CATEGORY_WORLD;
            case GLOBAL_DEV:
                return CATEGORY_GLOBAL_DEVELOPMENT;
            case ENVIRONMENT:
                return CATEGORY_ENVIRONMENT;
            default:
                return 0;
        }
    }
}
