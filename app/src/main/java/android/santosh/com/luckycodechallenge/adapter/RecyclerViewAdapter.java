package android.santosh.com.luckycodechallenge.adapter;

import android.content.Context;
import android.santosh.com.luckycodechallenge.R;
import android.santosh.com.luckycodechallenge.model.FlickrPost;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Santosh on 10/31/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static String TAG = RecyclerViewAdapter.class.getSimpleName();
    private Context context;
    private List<FlickrPost> originalFlickrPosts = new ArrayList<>();
    private List<FlickrPost> filteredFlickrPosts = new ArrayList<>();

    private String filterConstraint;

    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_list_item, parent, false);
        return new RecyclerViewItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof RecyclerViewItemViewHolder) {
            RecyclerViewItemViewHolder recyclerViewItemViewHolder = (RecyclerViewItemViewHolder) viewHolder;
            final FlickrPost flickrPost = originalFlickrPosts.get(position);
            if (TextUtils.isEmpty(flickrPost.getTitle()) || flickrPost.getTitle().trim().length() == 0) {
                recyclerViewItemViewHolder.titleTextView.setText("<NO TITLE PRESENT>");
            } else {
                recyclerViewItemViewHolder.titleTextView.setText(flickrPost.getTitle());
            }
            recyclerViewItemViewHolder.publishedTextView.setText(flickrPost.getPublished());

            String mediaUrl = "";
            if (flickrPost.getMedia() != null && !TextUtils.isEmpty(flickrPost.getMedia().getMediaLink())) {
                mediaUrl = flickrPost.getMedia().getMediaLink();
            }
            if (!TextUtils.isEmpty(mediaUrl)) {
                recyclerViewItemViewHolder.thumbNailRootView.setVisibility(View.VISIBLE);
                recyclerViewItemViewHolder.thumbNailImageView.setVisibility(View.VISIBLE);
                Picasso.with(context).load(mediaUrl).into(recyclerViewItemViewHolder.thumbNailImageView);
            } else {
                recyclerViewItemViewHolder.thumbNailRootView.setVisibility(View.GONE);
                recyclerViewItemViewHolder.thumbNailImageView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (filteredFlickrPosts != null) {
            return filteredFlickrPosts.size();
        } else {
            return 0;
        }
    }


    @Override
    public Filter getFilter() {
        return new TitleFilter();
    }

    private class TitleFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.d(TAG, "performFiltering, constraint: " + constraint);
            final FilterResults filterResults = new FilterResults();
            if (!TextUtils.isEmpty(constraint)) {
                String constraintText = constraint.toString().toLowerCase();
                final List<FlickrPost> flickrPosts = new ArrayList<FlickrPost>();
                for (FlickrPost flickrPost : originalFlickrPosts) {
                    if (flickrPost.getTitle().toLowerCase().contains(constraintText)) {
                        flickrPosts.add(flickrPost);
                    }
                }
                Log.d(TAG, "performFiltering, flickrPosts.size(): " + flickrPosts.size());
//                for (FlickrPost flickrPost : flickrPosts) {
//                    Log.d(TAG, "performFiltering, flickrPost.gettitle(): " + flickrPost.getTitle());
//                }
                filterResults.values = flickrPosts;
                filterResults.count = flickrPosts.size();
            } else {
                Log.d(TAG, "performFiltering, originalFlickrPosts.size(): " + originalFlickrPosts.size());
//                for (FlickrPost flickrPost : originalFlickrPosts) {
//                    Log.d(TAG, "performFiltering, originalFlickrPosts flickrPost.gettitle(): " + flickrPost.getTitle());
//                }
                filterResults.values = originalFlickrPosts;
                filterResults.count = originalFlickrPosts.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Log.d(TAG, "publishResults, constraint: " + constraint);
            filteredFlickrPosts = (List<FlickrPost>) results.values;
            filterConstraint = constraint != null ? constraint.toString() : null;
            if (filteredFlickrPosts != null && filteredFlickrPosts.size() > 0) {
                Log.d(TAG, "publishResults, final filtered list size(): " + filteredFlickrPosts.size());
                for (FlickrPost flickrPost : filteredFlickrPosts) {
                    Log.d(TAG, "publishResults, gettitle(): " + flickrPost.getTitle());
                }
            }
            notifyDataSetChanged();
        }
    }

    public class RecyclerViewItemViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView publishedTextView;
        View thumbNailRootView;
        ImageView thumbNailImageView;

        public RecyclerViewItemViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_textview);
            publishedTextView = itemView.findViewById(R.id.published_text_view);
            thumbNailRootView = itemView.findViewById(R.id.thumbnail_view_holder);
            thumbNailImageView = itemView.findViewById(R.id.thumbnail_imageview);
        }
    }

    public void swapPosts(List<FlickrPost> flickrPostList) {
        this.originalFlickrPosts.clear();
        this.originalFlickrPosts.addAll(flickrPostList);
        this.filteredFlickrPosts.addAll(flickrPostList);
        if (filterConstraint != null) {
            getFilter().filter(filterConstraint);
        } else {
            notifyDataSetChanged();
        }
    }

    private void add(FlickrPost flickrPost) {
        if (!originalFlickrPosts.contains(flickrPost)) {
            originalFlickrPosts.add(flickrPost);
            if (TextUtils.isEmpty(filterConstraint)) {
                notifyItemInserted(originalFlickrPosts.size() - 1);
            }
        }
        if (!filteredFlickrPosts.contains(flickrPost)) {
            filteredFlickrPosts.add(flickrPost);
            if (!TextUtils.isEmpty(filterConstraint)) {
                getFilter().filter(filterConstraint);
            }
        }
    }

    public void addAll(List<FlickrPost> flickrPostList) {
        for (FlickrPost flickrPost : flickrPostList) {
            add(flickrPost);
        }
    }
}
