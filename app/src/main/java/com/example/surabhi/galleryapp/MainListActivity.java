package com.example.surabhi.galleryapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainListActivity extends AppCompatActivity implements ViewSwitcher.ViewFactory, View.OnTouchListener {
    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 1024;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().toString() + "/album";

    public static final int PREVIEW_IMAGE_CHANGE_THRESHOLD = 150;
    public static final String SUPPORTED_FORMATS = ".jpg";
    public static final String KEY_FOLDER_PATH = "folderPath";
    public static final int IMAGE_SIZE = (int) Math.ceil(Math.sqrt(MAX_WIDTH * MAX_HEIGHT));

    private RecyclerView mRecyclerView;
    private ImageSwitcher mImageSwitcher;
    private ListRecyclerViewAdapter adapter;
    private List<GalleryItem> galleryList = new ArrayList<>();

    private int lastPreviewImagePosition = 0;
    private ArrayList<File> previewImages = new ArrayList<>();
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mImageSwitcher.setImageDrawable(
                    new BitmapDrawable(MainListActivity.this.getResources(), bitmap));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mImageSwitcher = (ImageSwitcher) findViewById(R.id.preview);
        mImageSwitcher.setFactory(this);
        mImageSwitcher.setOnTouchListener(this);

        adapter = new ListRecyclerViewAdapter(MainListActivity.this, galleryList, R.layout.list_row);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this));

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_STORAGE);
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    finish();
                }
                return;
            }
        }
    }

    private void init() {
        final File file = new File(ROOT_PATH);
        final File list[] = file.listFiles();
        for (int i = 0; i < list.length; i++) {
            final GalleryItem galleryItem = new GalleryItem();
            galleryItem.setTitle(list[i].getName());
            galleryItem.setImagePath(getFirstImageInFolder(list[i]));
            galleryList.add(galleryItem);
        }
        adapter.notifyDataSetChanged();
    }

    private String getFirstImageInFolder(File folder) {
        final File[] list = folder.listFiles();
        if (list != null) {
            for (File item : list) {
                if (!item.isDirectory() && item.getName().endsWith(SUPPORTED_FORMATS)) {
                    return item.getAbsolutePath();
                }
            }
        }
        return null;
    }

    private void showDetailsList(int position) {
        Intent intent = new Intent(this, DetailsListActivity.class);
        intent.putExtra(KEY_FOLDER_PATH, ROOT_PATH + "/" + galleryList.get(position).getTitle());
        startActivity(intent);
    }

    @Override
    public View makeView() {
        final ImageView imageView = new ImageView(this);
        imageView.setBackgroundColor(Color.WHITE);
        imageView.setPadding(5,5,5,5);
        imageView.setAdjustViewBounds(true);
        return imageView;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    private void showPreview(GalleryItem item) {
        mImageSwitcher.setVisibility(View.VISIBLE);
        mImageSwitcher.setInAnimation(null);
        mImageSwitcher.setOutAnimation(null);
        mImageSwitcher.setImageDrawable(null);

        final File[] files = new File(ROOT_PATH + "/" + item.getTitle()).listFiles();
        for (File file : files) {
            if (!file.isDirectory() && file.getName().endsWith(SUPPORTED_FORMATS)) {
                previewImages.add(file);
            }
        }
        Picasso.with(this).load(previewImages.get(lastPreviewImagePosition))
                .resize(IMAGE_SIZE, IMAGE_SIZE)
                .into(target);
    }

    private void changePreviewImage(boolean moveLeft) {
        if (previewImages.size() > 0) {
            final int tempPosition = lastPreviewImagePosition;
            if (moveLeft) {
                if (lastPreviewImagePosition + 1 < previewImages.size()) {
                    lastPreviewImagePosition++;
                    mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplication(),
                            R.anim.right_in));
                    mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplication(),
                            R.anim.left_out));
                }
            } else {
                if (lastPreviewImagePosition - 1 >= 0) {
                    lastPreviewImagePosition--;
                    mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplication(),
                            R.anim.left_in));
                    mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplication(),
                            R.anim.right_out));
                }
            }
            if (tempPosition != lastPreviewImagePosition) {
                Picasso.with(this).load(previewImages.get(lastPreviewImagePosition))
                        .resize(IMAGE_SIZE, IMAGE_SIZE)
                        .into(target);
            }
        }
    }

    private void closePreview() {
        lastPreviewImagePosition = 0;
        mImageSwitcher.setVisibility(View.GONE);
        previewImages.clear();
    }

    class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector mGestureDetector;
        private float lastX = 0;
        public RecyclerItemClickListener(Context context) {
            mGestureDetector = new GestureDetector(context,
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(MotionEvent motionEvent) {
                            return true;
                        }

                        @Override
                        public void onLongPress(MotionEvent motionEvent) {
                            final View childView = mRecyclerView.findChildViewUnder(
                                    motionEvent.getX(), motionEvent.getY());
                            final GalleryItem item = galleryList.get(
                                    mRecyclerView.getChildAdapterPosition(childView));
                            showPreview(item);
                        }
                    });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mGestureDetector.onTouchEvent(e)) {
                showDetailsList(view.getChildAdapterPosition(childView));
            }
            if (e.getAction() == MotionEvent.ACTION_UP) {
                closePreview();
            } else {
                if (Math.abs(e.getX() - lastX) > PREVIEW_IMAGE_CHANGE_THRESHOLD) {
                    boolean moveLeft = e.getX() < lastX;
                    changePreviewImage(moveLeft);
                    lastX = e.getX();
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {

        }
    }
}
