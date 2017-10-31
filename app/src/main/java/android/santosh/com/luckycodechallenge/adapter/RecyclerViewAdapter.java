package android.santosh.com.luckycodechallenge.adapter;

import android.content.Context;
import android.santosh.com.luckycodechallenge.R;
import android.santosh.com.luckycodechallenge.model.FlickrPost;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Santosh on 10/31/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String TAG = RecyclerViewAdapter.class.getSimpleName();
    private Context context;
    private List<FlickrPost> flickrPosts = new ArrayList<>();

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
            final FlickrPost flickrPost = flickrPosts.get(position);
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
        return flickrPosts.size();
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
        this.flickrPosts.clear();
        this.flickrPosts.addAll(flickrPostList);
        notifyDataSetChanged();
    }

    public void add(FlickrPost flickrPost) {
        if (!flickrPosts.contains(flickrPost)) {
            flickrPosts.add(flickrPost);
            notifyItemInserted(flickrPosts.size() - 1);
        }
    }

    public void addAll(List<FlickrPost> flickrPostList) {
        for (FlickrPost flickrPost : flickrPostList) {
            add(flickrPost);
        }
    }
}
