package android.santosh.com.luckycodechallenge.listeners;

import android.santosh.com.luckycodechallenge.model.FlickrPost;

import java.util.List;

/**
 * Created by Santosh on 10/30/17.
 */

public interface MainControllerListener {
    void onFetchSuccess(List<FlickrPost> flickrPostList);
    void onFetchFailure();
    void onPostupdate(List<FlickrPost> flickrPostList);
}
