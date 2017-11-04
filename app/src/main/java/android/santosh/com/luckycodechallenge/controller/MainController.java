package android.santosh.com.luckycodechallenge.controller;

import android.os.Handler;
import android.santosh.com.luckycodechallenge.listeners.MainControllerListener;
import android.santosh.com.luckycodechallenge.model.FlickrPost;
import android.santosh.com.luckycodechallenge.response.FlickrResponse;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Santosh on 10/30/17.
 */

public class MainController {
    private static String TAG = MainController.class.getSimpleName();
    private static String FLICKR_POST_LIST_URL = "https://api.flickr.com/services/feeds/photos_public.gne?format=json&tags=%s";

    private Handler uiHandler;
    private ExecutorService executorService;
    private OkHttpClient client;
    private Gson gson;
    private List<MainControllerListener> mainControllerListeners = Collections.synchronizedList(new ArrayList<MainControllerListener>());
    private List<FlickrPost> flickrPosts = new ArrayList<>();
    private final Object flickrPostLock = new Object();

    public MainController(Handler uiHandler) {
        this.executorService = Executors.newSingleThreadExecutor();
        this.uiHandler = uiHandler;
        this.client = new OkHttpClient();
        this.gson = new GsonBuilder().create();
    }

    public void fetchPost(final String tag, final boolean isPaging) {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        String finalTag;
                        if (TextUtils.isEmpty(tag)) {
                            finalTag = "";
                        } else {
                            finalTag = tag;
                        }
                        Request request = new Request.Builder()
                                .url(String.format(Locale.US, FLICKR_POST_LIST_URL, finalTag))
                                .build();
                        Response response = client.newCall(request).execute();
                        String stringyfiedJson = new String(response.body().bytes(), "UTF-8");
                        switch (response.code()) {
                            case 200:
                                //Hack to remove weird JSON structure. hit the endpoint on postman and check the result.
                                if (stringyfiedJson.contains("jsonFlickrFeed(")) {
                                    stringyfiedJson = stringyfiedJson.replace("jsonFlickrFeed(", "");
                                    int lastIndex = stringyfiedJson.lastIndexOf(")");
                                    if (lastIndex > 0) {
                                        stringyfiedJson = stringyfiedJson.substring(0, lastIndex);
                                    }
                                }

                                FlickrResponse flickrResponse = gson.fromJson(stringyfiedJson, FlickrResponse.class);
                                List<FlickrPost> flickrPostList = Arrays.asList(flickrResponse.getFlickrPosts());
                                if (isPaging) {
                                    //As the API is giving is same list of posts on some request, doing this to prevent duplicates.
                                    for (FlickrPost flickrPost : flickrPostList) {
                                        if (!flickrPosts.contains(flickrPost)) {
                                            flickrPosts.add(flickrPost);
                                        }
                                    }
                                    notifyFlickrPostListUpdateSuccess(flickrPostList);
                                } else {
                                    Log.d(TAG,"in the else block");
                                    flickrPosts.clear();
                                    flickrPosts.addAll(flickrPostList);
                                    notifyFlickrPostListFetchSuccess(flickrPosts);
                                }
                                break;
                            default:
                                notifyFlickrPostListFetchFailure();
                                break;
                        }
                    } catch (IOException iex) {
                        notifyFlickrPostListFetchFailure();
                    }
                }
            });
        } else {
            notifyFlickrPostListFetchFailure();
        }
    }

    public List<FlickrPost> getFlickrPosts() {
        synchronized (flickrPostLock) {
            return new ArrayList<>(flickrPosts);
        }
    }

    public void resetData() {
        flickrPosts.clear();
    }

    public void addMainControllerListener(MainControllerListener mainControllerListener) {
        if (mainControllerListeners != null && !mainControllerListeners.contains(mainControllerListener)) {
            mainControllerListeners.add(mainControllerListener);
        }
    }

    public void removeMainControllerListener(MainControllerListener mainControllerListener) {
        if (mainControllerListeners != null && mainControllerListeners.contains(mainControllerListener)) {
            mainControllerListeners.remove(mainControllerListener);
        }
    }

    private void notifyFlickrPostListFetchSuccess(final List<FlickrPost> flickrPostList) {
        if (mainControllerListeners != null && mainControllerListeners.size() > 0) {
            for (final MainControllerListener mainControllerListener : mainControllerListeners) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mainControllerListener.onFetchSuccess(flickrPostList);
                    }
                });
            }
        }
    }

    private void notifyFlickrPostListUpdateSuccess(final List<FlickrPost> flickrPostList) {
        if (mainControllerListeners != null && mainControllerListeners.size() > 0) {
            for (final MainControllerListener mainControllerListener : mainControllerListeners) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mainControllerListener.onPostupdate(flickrPostList);
                    }
                });
            }
        }
    }

    private void notifyFlickrPostListFetchFailure() {
        if (mainControllerListeners != null && mainControllerListeners.size() > 0) {
            for (final MainControllerListener mainControllerListener : mainControllerListeners) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mainControllerListener.onFetchFailure();
                    }
                });
            }
        }
    }
}
