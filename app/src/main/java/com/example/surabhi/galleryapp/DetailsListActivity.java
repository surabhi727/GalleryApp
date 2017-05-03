package com.example.surabhi.galleryapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Surabhi on 5/2/17.
 */

public class DetailsListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ListRecyclerViewAdapter adapter;
    private List<GalleryItem> galleryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_list);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListRecyclerViewAdapter(DetailsListActivity.this, galleryList, R.layout.detail_row);
        mRecyclerView.setAdapter(adapter);

        final String path = getIntent().getStringExtra(MainListActivity.KEY_FOLDER_PATH);
        if(TextUtils.isEmpty(path)) {
           finish();
        }
        final File file = new File(path) ;
        final File list[] = file.listFiles();
        for( int i = 0; i < list.length; i++) {
            if(!list[i].isDirectory() &&
                    list[i].getName().endsWith(MainListActivity.SUPPORTED_FORMATS)) {
                GalleryItem galleryItem = new GalleryItem();
                galleryItem.setTitle(list[i].getName());
                galleryItem.setImagePath(list[i].getAbsolutePath());
                galleryList.add(galleryItem);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
