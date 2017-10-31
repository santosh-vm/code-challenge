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

    @SerializedName("media")
    Media media;

    public String getTitle() {
        return title;
    }

    public String getPublished() {
        return published;
    }

    public Media getMedia() {
        return media;
    }

    public class Media{
        @SerializedName("m")
        String mediaLink;

        public String getMediaLink() {
            return mediaLink;
        }
    }
}
