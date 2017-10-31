package android.santosh.com.luckycodechallenge.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Santosh on 10/30/17.
 */

public class FlickrPost {
    @SerializedName("title")
    String title;

    @SerializedName("published")
    String published;

    @SerializedName("link")
    String mediaLink;

    public String getTitle() {
        return title;
    }

    public String getPublished() {
        return published;
    }

    public String getMediaLink() {
        return mediaLink;
    }
}
