package android.santosh.com.luckycodechallenge.activity;

import android.santosh.com.luckycodechallenge.R;
import android.santosh.com.luckycodechallenge.adapter.RecyclerViewAdapter;
import android.santosh.com.luckycodechallenge.listeners.MainControllerListener;
import android.santosh.com.luckycodechallenge.model.FlickrPost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener, MainControllerListener {
    private static String TAG = MainActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private TextView errorMessageTextView;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private LinearLayoutManager recyclerViewLinearLayoutManger;

    private String searchQuery = null;

    private boolean isLoading = false;
    int previousVisibleItems, visibleItemCount, totalItemCount;
    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0) {
                visibleItemCount = recyclerViewLinearLayoutManger.getChildCount();
                totalItemCount = recyclerViewLinearLayoutManger.getItemCount();
                previousVisibleItems = recyclerViewLinearLayoutManger.findFirstVisibleItemPosition();
                //Log.d(TAG, "onScrolled totalItemCount: " + totalItemCount);
                if (!isLoading && ((visibleItemCount + previousVisibleItems) >= totalItemCount) && totalItemCount < 50) {
                    //Log.d(TAG, "Let's Fetch more posts");
                    isLoading = true;
                    appAPI.getMainController().fetchPost();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appAPI.getMainController().addMainControllerListener(this);
        bindUIElements();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appAPI.getMainController().getFlickrPosts().size() > 0) {
            recyclerViewAdapter.getFilter().filter(searchQuery);
        }
    }

    private void bindUIElements() {
        //Progress Bar
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //TextView
        errorMessageTextView = (TextView) findViewById(R.id.error_message_text_view);
        errorMessageTextView.setVisibility(View.GONE);

        //RecyclerView
        recyclerViewAdapter = new RecyclerViewAdapter(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerViewLinearLayoutManger = new LinearLayoutManager(this);
        recyclerViewLinearLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(recyclerViewLinearLayoutManger);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        List<FlickrPost> redditPosts = appAPI.getMainController().getFlickrPosts();
        //Log.d(TAG,"onCreate redditPosts.size: "+redditPosts.size());
        if (redditPosts.size() > 0) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerViewAdapter.swapPosts(redditPosts);
        } else {
            recyclerView.setVisibility(View.GONE);
            appAPI.getMainController().fetchPost();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main_activity, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appAPI.getMainController().removeMainControllerListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //TODO: implement filter logic.
        Log.d(TAG, "onQueryTextSubmit, query: " + query);
        searchQuery = query;
        recyclerViewAdapter.getFilter().filter(searchQuery);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange, newText: " + newText);
        searchQuery = newText;
        recyclerViewAdapter.getFilter().filter(searchQuery);
        return true;
    }

    @Override
    public void onFetchSuccess(List<FlickrPost> flickrPostList) {
        progressBar.setVisibility(View.GONE);
        errorMessageTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewAdapter.addAll(flickrPostList);
        recyclerViewAdapter.getFilter().filter(searchQuery);
        isLoading = false;
    }

    @Override
    public void onFetchFailure() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        if (recyclerViewAdapter.getItemCount() <= 0) {
            errorMessageTextView.setVisibility(View.VISIBLE);
        } else {
            errorMessageTextView.setVisibility(View.GONE);
        }
        isLoading = false;
    }
}
