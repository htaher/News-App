package com.example.android.myuknews;


/**
 * An {@link UKNews} object contains information related to a single news.
 */
public class UKNews {

    /**
     * Title of the news
     */
    private String mTitle;

    /**
     * Section name of the news
     */
    private String mSectionName;

    /**
     * Date of the news
     */
    private String mDate;

    /**
     * Website URL of the news
     */
    private String mUrl;

    /**
     * Constructs a new {@link UKNews} object.
     *
     * @param title       is the title of the news
     * @param sectionName is the section where the news belong to
     * @param date        is the date when the news was published
     * @param url         is the website URL to find more details about the earthquake
     */
    public UKNews(String title, String sectionName, String date, String url) {
        mTitle = title;
        mSectionName = sectionName;
        mDate = date;
        mUrl = url;
    }

    /**
     * Returns the title of the news.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the section name of the news.
     */
    public String getSectionName() {
        return mSectionName;
    }

    /**
     * Returns the date of the news.
     */
    public String getDates() {
        return mDate;
    }

    /**
     * Returns the website URL to find more information about the news.
     */
    public String getUrl() {
        return mUrl;
    }
}
