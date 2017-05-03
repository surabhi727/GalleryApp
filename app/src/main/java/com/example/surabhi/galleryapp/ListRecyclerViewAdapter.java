package com.example.surabhi.galleryapp;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Surabhi on 5/2/17.
 */

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.CustomViewHolder> {

    private List<GalleryItem> galleryItemList;
    private Context mContext;
    private int rowView;

    public ListRecyclerViewAdapter(Context context, List<GalleryItem> galleryItemList, int rowView) {
        this.galleryItemList = galleryItemList;
        this.mContext = context;
        this.rowView = rowView;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(rowView, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        final GalleryItem item = galleryItemList.get(i);
        customViewHolder.update(item);
    }

    @Override
    public int getItemCount() {
        return (null != galleryItemList ? galleryItemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.image);
            this.textView = (TextView) view.findViewById(R.id.title);
        }

        public void update(GalleryItem item) {
            if (!TextUtils.isEmpty(item.getImagePath())) {
                Uri uri = Uri.fromFile(new File(item.getImagePath()));
                Picasso.with(mContext).load(uri)
                        .resize(MainListActivity.IMAGE_SIZE, MainListActivity.IMAGE_SIZE)
                        .centerInside().into(imageView);
            }
            textView.setText(item.getTitle());
        }
    }
}
