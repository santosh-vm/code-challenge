package android.santosh.com.luckycodechallenge.response;

import android.santosh.com.luckycodechallenge.model.FlickrPost;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Santosh on 10/30/17.
 */

public class FlickrResponse {

    @SerializedName("items")
    FlickrPost[] flickrPosts;

    public FlickrPost[] getFlickrPosts() {
        return flickrPosts;
    }
}
