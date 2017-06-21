package com.example.android.myuknews;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Helper methods related to requesting and receiving news data from the Guardian API.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the the Guardian API dataset and return a list of {@link UKNews} objects.
     */
    public static List<UKNews> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link UKNews}s
        List<UKNews> news = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link UKNews}s
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link UKNews} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<UKNews> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<UKNews> newsArrayList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject basejsonResults = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results of news.
            JSONArray newsArray = basejsonResults.getJSONArray("results");

            // For each news in the newsArray, create an {@link UKNews} object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single news at position i within the list
                JSONObject result = newsArray.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String title = result.getString("webTitle");

                // Extract the value for the key called "sectionName"
                String sectionName = result.getString("sectionName");

                // Extract the value for the key called "webPublicationDate"
                String date = result.getString("webPublicationDate");
                // Format the date string (i.e. "Mar 3, 1984")
                date = formatDate(date);

                // Extract the value for the key called "webUrl"
                String url = result.getString("webUrl");

                // Create a new {@link UKNews} object with the title, section name, date,
                // and url from the JSON response.
                UKNews news = new UKNews(title, sectionName, date, url);

                // Add the new {@link UKNews} to the list of news.
                newsArrayList.add(news);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        // Return the list of earthquakes
        return newsArrayList;
    }

    /**
     * Format and return the string (i.e. "Mar 3, 1984") from Json response.
     */
    private static String formatDate(String receivedDate) {
        // Create a String with the way how the date response is written
        String jsonDateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";

        // Format the date
        SimpleDateFormat formatDate = new SimpleDateFormat(jsonDateFormat, Locale.UK);
        try {
            Date parsedDate = formatDate.parse(receivedDate);
            String finalFormat = "MMM d, yyy";
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalFormat, Locale.UK);
            return finalDateFormatter.format(parsedDate);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error parsing JSON Date: ", e);
            return "";
        }
    }

}
